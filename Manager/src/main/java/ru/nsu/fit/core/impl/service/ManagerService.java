package ru.nsu.fit.core.impl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.nsu.fit.config.crackhash.CrackhashProperties;
import ru.nsu.fit.core.api.ProducerService;
import ru.nsu.fit.core.api.dto.ManagerCrackRequest;
import ru.nsu.fit.core.api.dto.RequestStatus;
import ru.nsu.fit.core.api.dto.WorkerCrackRequest;
import ru.nsu.fit.core.api.dto.WorkerResponse;
import ru.nsu.fit.core.impl.data.TaskDao;
import ru.nsu.fit.core.impl.data.TaskRepository;
import ru.nsu.fit.core.impl.domain.*;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

@Service
public class ManagerService {

    private final Logger logger = LoggerFactory.getLogger(Manager.class);

    private final CrackhashProperties properties;
    private final Manager manager;
    private final TaskRepository taskRepository;
    private final ProducerService producerService;

    public ManagerService(CrackhashProperties properties,
                          Manager manager,
                          TaskRepository taskRepository,
                          ProducerService producerService) {
        this.properties = properties;
        this.manager = manager;
        this.taskRepository = taskRepository;
        this.producerService = producerService;
    }

    public UUID splitTask(ManagerCrackRequest crackRequest) {
        UUID requestId = manager.registerTaskRequest();
        logger.info("Sending task {} to worker", requestId);
        sendTaskToWorkers(requestId, crackRequest.hash(), crackRequest.maxLength());
        saveTask(new TaskDao(requestId, crackRequest.hash(), crackRequest.maxLength()));
        return requestId;
    }

    private void sendTaskToWorkers(UUID requestId, String hash, int maxLength) {
        Integer workersNumber = properties.getWorkersNumber();
        ExecutorService executorService = Executors.newFixedThreadPool(workersNumber);
        for (int workerIndex = 0; workerIndex < workersNumber; workerIndex++) {
            int finalWorkerIndex = workerIndex;
            executorService.submit(() -> {
                try {
                    WorkerCrackRequest crackRequest = new WorkerCrackRequest(
                            requestId,
                            hash,
                            maxLength,
                            finalWorkerIndex + 1,
                            workersNumber
                    );
                    producerService.sendCrackRequestToWorker(crackRequest, finalWorkerIndex + 1);
                } catch (RuntimeException e) {
                    if (e instanceof IllegalStateException) {
                        manager.setErrorStatus(requestId);
                    }
                }
            });
        }
    }

    private void saveTask(TaskDao task) {
        taskRepository.save(task);
    }

    public RequestStatus getTaskStatus(UUID requestId) {
        Task task = manager.getTask(requestId);
        if (task == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        return new RequestStatus(task.status(), task.data().toArray(new String[0]));
    }

    public void processWorkerResponse(WorkerResponse response) {
        addWordsToTask(response);
        if (manager.getTask(response.requestId()).status() == Status.READY) {
            deleteTask(response.requestId());
        }
    }

    private void addWordsToTask(WorkerResponse response) {
        Predicate<Integer> taskDone = workersCompleted ->
                workersCompleted.equals(properties.getWorkersNumber());
        try {
            manager.mergeWords(response.requestId(), response.data(), taskDone);
        } catch (NoSuchTaskInMemoryException e) {
            TaskDao task = taskRepository.findById(response.requestId())
                    .orElseThrow(NoSuchTaskPersistedException::new);
            manager.restoreTask(task, response.data(), taskDone);
        }
    }

    private void deleteTask(UUID requestId) {
        TaskDao task = taskRepository.findById(requestId)
                .orElseThrow(NoSuchTaskPersistedException::new);
        taskRepository.delete(task);
    }
}
