package ru.nsu.fit.core.impl.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import ru.nsu.fit.core.api.ProducerService;
import ru.nsu.fit.core.api.dto.RequestStatus;
import ru.nsu.fit.core.api.dto.WorkerCrackRequest;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@org.springframework.stereotype.Service
public class Manager {
    private final Logger logger = LoggerFactory.getLogger(Manager.class);

    public final static int WORKERS_NUMBER = 2;
    private final static Duration timeout = Duration.of(60, ChronoUnit.SECONDS);
    private final Map<UUID, Task> tasks = new ConcurrentHashMap<>();

    private final ProducerService producerService;

    public Manager(ProducerService producerService) {
        this.producerService = producerService;
    }

    public UUID splitTask(String hash, int maxLength) {
        UUID requestId = registerTaskRequest();
        logger.info("Sending task {} to workers", requestId);
        sendTaskToWorker(requestId, hash, maxLength);
        return requestId;
    }

    private void setErrorStatus(UUID requestId) {
        Task task = tasks.get(requestId);
        tasks.put(requestId, task.timeoutExceeded());
    }

    private UUID registerTaskRequest() {
        UUID requestId = UUID.randomUUID();
        tasks.put(requestId, Task.defaultTask());
        return requestId;
    }

    private void sendTaskToWorker(UUID requestId, String hash, int maxLength) {
        ExecutorService executorService = Executors.newFixedThreadPool(WORKERS_NUMBER);
        for (int workerIndex = 0; workerIndex < WORKERS_NUMBER; workerIndex++) {
            int finalWorkerIndex = workerIndex;
            executorService.submit(() -> {
                try {
                    WorkerCrackRequest crackRequest = new WorkerCrackRequest(requestId, hash, maxLength, finalWorkerIndex + 1, WORKERS_NUMBER);
                    producerService.sendCrackRequestToWorker(crackRequest, finalWorkerIndex + 1);
                } catch (RuntimeException e) {
                    if (e instanceof IllegalStateException) {
                        setErrorStatus(requestId);
                    }
                }
            });
        }
    }

    public void mergeWords(UUID requestId, Set<String> foundWords) {
        Task task = tasks.get(requestId);
        tasks.put(requestId, task.workerCompleted(foundWords, WORKERS_NUMBER));
    }

    public RequestStatus getTaskStatus(UUID requestId) {
        Task task = tasks.get(requestId);
        if (task == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        return new RequestStatus(task.status(), task.data().toArray(new String[0]));
    }
}
