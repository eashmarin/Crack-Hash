package ru.nsu.fit.core.impl.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.fit.core.api.ProducerService;
import ru.nsu.fit.core.api.dto.WorkerResponse;

import java.util.Set;
import java.util.UUID;

@org.springframework.stereotype.Service
public class Worker {

    private final ProducerService producerService;
    private final HashCracker hashCracker;

    private final Logger logger = LoggerFactory.getLogger(Worker.class);

    public Worker(ProducerService producerService, HashCracker hashCracker) {
        this.producerService = producerService;
        this.hashCracker = hashCracker;
    }

    public void processManagerTask(UUID requestId, String desiredHash, int maxLength, int partNumber, int partCount) {
        logger.info("Worker #{} is starting processing manager task", partNumber);
        Set<String> hashEqualWords = hashCracker.findHashEqualWords(desiredHash, maxLength, partNumber, partCount);
        logger.info("Worker #{} has finished, found words: {}", partNumber, hashEqualWords.stream().reduce((a, b) -> a + b));
        sendWordsToManager(requestId, hashEqualWords);
    }

    private void sendWordsToManager(UUID requestId, Set<String> words) {
        producerService.sendWordsToManager(new WorkerResponse(requestId, words));
    }
}