package ru.nsu.fit.core.impl.service;

import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpIOException;
import org.springframework.amqp.rabbit.listener.ConsumeOkEvent;
import org.springframework.amqp.rabbit.listener.ListenerContainerConsumerFailedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.nsu.fit.config.crackhash.CrackhashProperties;
import ru.nsu.fit.core.api.ProducerService;
import ru.nsu.fit.core.api.dto.ManagerCrackRequest;
import ru.nsu.fit.core.api.dto.RequestStatus;
import ru.nsu.fit.core.api.dto.WorkerCrackRequest;
import ru.nsu.fit.core.api.dto.WorkerResponse;
import ru.nsu.fit.core.impl.data.*;
import ru.nsu.fit.core.impl.domain.Manager;
import ru.nsu.fit.core.impl.domain.NoSuchTaskPersistedException;
import ru.nsu.fit.core.impl.domain.Status;
import ru.nsu.fit.core.impl.domain.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

@Service
public class ManagerService {

    private final Logger logger = LoggerFactory.getLogger(Manager.class);

    private final CrackhashProperties properties;
    private final Manager manager;
    private final ProducerService producerService;
    private final TaskRepository taskRepository;
    private final FoundWordsRepository foundWordsRepository;
    private final PendingTaskRepository pendingTaskRepository;

    private Boolean queueShutdown = false;

    public ManagerService(CrackhashProperties properties,
                          Manager manager,
                          TaskRepository taskRepository,
                          FoundWordsRepository foundWordsRepository,
                          ProducerService producerService,
                          PendingTaskRepository pendingTaskRepository) {
        this.properties = properties;
        this.manager = manager;
        this.taskRepository = taskRepository;
        this.foundWordsRepository = foundWordsRepository;
        this.producerService = producerService;
        this.pendingTaskRepository = pendingTaskRepository;
    }

    public UUID splitTask(ManagerCrackRequest crackRequest) {
        UUID requestId = manager.registerTaskRequest();
        logger.info("Sending task {} to worker", requestId);
        sendTaskToWorkers(requestId, crackRequest.hash(), crackRequest.maxLength());
        if (queueShutdown) {
            pendingTaskRepository.save(new PendingTaskDao(
                    requestId,
                    crackRequest.hash(),
                    crackRequest.maxLength()
            ));
        }
        taskRepository.save(new TaskDao(
                requestId,
                crackRequest.hash(),
                crackRequest.maxLength()
        ));
        return requestId;
    }

    private void sendTaskToWorkers(UUID requestId, String hash, int maxLength) {
        Integer workersNumber = properties.getWorkersNumber();
        ExecutorService executorService = Executors.newFixedThreadPool(workersNumber);
        for (int workerIndex = 0; workerIndex < workersNumber; workerIndex++) {
            int finalWorkerIndex = workerIndex;
            executorService.submit(() -> {
                WorkerCrackRequest crackRequest = new WorkerCrackRequest(
                        requestId,
                        hash,
                        maxLength,
                        finalWorkerIndex + 1,
                        workersNumber
                );
                producerService.sendCrackRequestToWorker(crackRequest, finalWorkerIndex + 1);
            });
        }
    }

    private void saveTask(TaskDao task) {

    }

    public RequestStatus getTaskStatus(UUID requestId) {
        Optional<FoundWordsDao> foundWordsDao = foundWordsRepository.findById(requestId);
        if (foundWordsDao.isEmpty()) {
            taskRepository.findById(requestId).
                    orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
            return new RequestStatus(Status.IN_PROGRESS, new String[0]);
        }

        return new RequestStatus(
                foundWordsDao.get().getStatus(),
                foundWordsDao.get().getWords().toArray(new String[0])
        );
    }

    public void processWorkerResponse(WorkerResponse response) {
        logger.info("processing worker's response: id = {}, data = {}", response.requestId(), response.data());
        addWordsToTask(response);
        deleteTaskIfCompleted(response.requestId());
    }

    private void addWordsToTask(WorkerResponse response) {

        Predicate<Integer> taskDone = workersCompleted ->
                workersCompleted.equals(properties.getWorkersNumber());

        Optional<FoundWordsDao> foundWordsDao = foundWordsRepository.findById(response.requestId());
        Task taskWithMergedWords = manager.mergeWords(foundWordsDao, response.data(), taskDone);
        foundWordsRepository.save(new FoundWordsDao(
                response.requestId(),
                taskWithMergedWords.data(),
                taskWithMergedWords.status())
        );
    }

    private void deleteTaskIfCompleted(UUID requestId) {
        Optional<FoundWordsDao> foundWordsDao = foundWordsRepository.findById(requestId);
        if (foundWordsDao.isEmpty()) {
            return;
        }
        if (foundWordsDao.get().getStatus() == Status.READY) {
            TaskDao task = taskRepository.findById(requestId)
                    .orElseThrow(NoSuchTaskPersistedException::new);
            taskRepository.delete(task);
        }
    }

    public void setQueueShutdown(Boolean value) {
        this.queueShutdown = value;
    }

    public void sendPendingTasks() {
        List<PendingTaskDao> tasks = pendingTaskRepository.findAll();
        tasks.forEach(task ->
                sendTaskToWorkers(task.getRequestId(), task.getHash(), task.getMaxLength())
        );
    }

    public void clearPendingTasks() {
        pendingTaskRepository.deleteAll();
    }

    @EventListener
    public void events(ListenerContainerConsumerFailedEvent event) {
        if (event.getThrowable().getClass().equals(ShutdownSignalException.class)) {
            logger.error("SHUTDOWN_ALERT");
            setQueueShutdown(true);
        }

        if (event.getThrowable().getClass().equals(AmqpIOException.class)) {
            logger.warn("something went wrong: {}", event);
            //sendPendingTasks();
        }
    }

    @EventListener
    public void consumeOkEvent(ConsumeOkEvent event) {
        logger.warn("rrrrrrrrrrrr: {}", event);
        sendPendingTasks();
        clearPendingTasks();
        setQueueShutdown(false);
    }
}
