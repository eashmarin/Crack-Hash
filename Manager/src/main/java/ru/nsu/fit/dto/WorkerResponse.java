package ru.nsu.fit.dto;

import java.util.Set;
import java.util.UUID;

public record WorkerResponse(UUID requestId, Set<String> data) {
}
