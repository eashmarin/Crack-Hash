package ru.nsu.fit;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nsu.fit.dto.RequestStatus;
import ru.nsu.fit.dto.WorkerCrackRequest;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@org.springframework.stereotype.Service
public class Service {
    public final static int WORKERS_NUMBER = 1;

    private final Map<UUID, Task> tasks = new ConcurrentHashMap<>();
    private final WebClient webClient;

    public Service() {
        this.webClient = WebClient.builder()
                .baseUrl("http://worker:8080/internal/api/worker/hash/crack/task")
                .build();
    }


    public UUID splitTask(String hash, int maxLength) {
        UUID requestId = UUID.randomUUID();
        tasks.put(requestId, Task.defaultTask());

        webClient.post()
                .body(BodyInserters.fromValue(new WorkerCrackRequest(requestId, hash, maxLength, 1, 1)))
                .retrieve()
                .toBodilessEntity()
                .block();

        return requestId;
    }

    public void mergeWords(UUID requestId, Set<String> foundWords) {
        Task task = tasks.get(requestId);
        tasks.put(requestId, task.workerCompleted(foundWords));
    }

    public RequestStatus getTaskStatus(UUID requestId) {
        Task task = tasks.get(requestId);
        return new RequestStatus(task.status(), task.data().toArray(new String[0]));
    }
}
