package ru.nsu.fit.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nsu.fit.dto.WorkerResponse;
import ru.nsu.fit.port.WorkerUrl;

import java.util.Set;
import java.util.UUID;

@org.springframework.stereotype.Service
public class Worker {

    private final WebClient webClient;
    private final HashCracker hashCracker;

    private final Logger logger = LoggerFactory.getLogger(Worker.class);

    public Worker(HashCracker hashCracker) {
        this.hashCracker = hashCracker;
        this.webClient = WebClient.builder()
                .baseUrl(WorkerUrl.WORKER_RESPONSE)
                .build();
    }

    public void processManagerTask(UUID requestId, String desiredHash, int maxLength, int partNumber, int partCount) {
        logger.info("Worker #{} is starting processing manager task", partNumber);
        Set<String> hashEqualWords = hashCracker.findHashEqualWords(desiredHash, maxLength, partNumber, partCount);
        logger.info("Worker #{} has finished, found words: {}", partNumber, hashEqualWords.stream().reduce((a, b) -> a + b));
        sendWordsToManager(requestId, hashEqualWords);
    }

    private void sendWordsToManager(UUID requestId, Set<String> words) {
        webClient.patch()
                .bodyValue(new WorkerResponse(requestId, words))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}