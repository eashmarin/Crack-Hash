package ru.nsu.fit.core.api.dto;

import java.io.Serializable;
import java.util.UUID;

public record WorkerCrackRequest(
        UUID requestId,
        String hash,
        Integer maxLength,
        Integer partNumber,
        Integer partCount
) implements Serializable {
}