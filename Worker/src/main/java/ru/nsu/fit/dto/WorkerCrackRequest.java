package ru.nsu.fit.dto;

import java.util.UUID;

public record WorkerCrackRequest(
        UUID requestId,
        String hash,
        Integer maxLength,
        Integer partNumber,
        Integer partCount
) {
}