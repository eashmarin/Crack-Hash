package ru.nsu.fit.dto;

import java.util.UUID;

public record WorkerCrackRequest(
        UUID requestId,
        String hash,
        int maxLength,
        int partNumber,
        int partCount
) {
}
