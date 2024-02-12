package ru.nsu.fit.domain;

import org.springframework.web.reactive.function.client.WebClient;
import ru.nsu.fit.dto.RequestStatus;
import ru.nsu.fit.dto.WorkerCrackRequest;
import ru.nsu.fit.port.ManagerUrl;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@org.springframework.stereotype.Service
public class Manager {
    public final static int WORKERS_NUMBER = 1;
    private final Map<UUID, Task> tasks = new ConcurrentHashMap<>();
    private final WebClient webClient;

    public Manager() {
        this.webClient = WebClient.builder()
                .baseUrl(ManagerUrl.WORKER_CRACK_REQUEST)
                .build();
    }

    public UUID splitTask(String hash, int maxLength) {
        UUID requestId = registerTaskRequest();
        sendTaskToWorker(requestId, hash, maxLength);
        return requestId;
    }

    private UUID registerTaskRequest() {
        UUID requestId = UUID.randomUUID();
        tasks.put(requestId, Task.defaultTask());
        return requestId;
    }

    private void sendTaskToWorker(UUID requestId, String hash, int maxLength) {
        webClient.post()
                .bodyValue(new WorkerCrackRequest(requestId, hash, maxLength, 1, 1))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void mergeWords(UUID requestId, Set<String> foundWords) {
        Task task = tasks.get(requestId);
        tasks.put(requestId, task.workerCompleted(foundWords, WORKERS_NUMBER));
    }

    public RequestStatus getTaskStatus(UUID requestId) {
        Task task = tasks.get(requestId);
        return new RequestStatus(task.status(), task.data().toArray(new String[0]));
    }
}
