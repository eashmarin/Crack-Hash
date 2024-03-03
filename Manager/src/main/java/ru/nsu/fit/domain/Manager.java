package ru.nsu.fit.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nsu.fit.dto.RequestStatus;
import ru.nsu.fit.dto.WorkerCrackRequest;
import ru.nsu.fit.port.ManagerUrl;

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
    public final static int WORKERS_NUMBER = 2;
    private final static Duration timeout = Duration.of(4, ChronoUnit.SECONDS);
    private final Map<UUID, Task> tasks = new ConcurrentHashMap<>();

    public Manager() {

    }

    public UUID splitTask(String hash, int maxLength) {
        UUID requestId = registerTaskRequest();
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
                    WebClient.builder()
                            .baseUrl(constructWorkerURL(finalWorkerIndex + 1))
                            .build()
                            .post()
                            .bodyValue(new WorkerCrackRequest(requestId, hash, maxLength, finalWorkerIndex + 1, WORKERS_NUMBER))
                            .retrieve()
                            .toBodilessEntity()
                            .block(timeout);

                    System.out.println("\n");
                    System.out.println(finalWorkerIndex);
                    System.out.println(constructWorkerURL(finalWorkerIndex + 1));
                    System.out.println("\n");
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                    if (e instanceof IllegalStateException) {
                        setErrorStatus(requestId);
                    }
                }
            });
        }
    }

    private String constructWorkerURL(int workerNumber) {
        return String.format(ManagerUrl.WORKER_CRACK_REQUEST_TEMPLATE, workerNumber);
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
