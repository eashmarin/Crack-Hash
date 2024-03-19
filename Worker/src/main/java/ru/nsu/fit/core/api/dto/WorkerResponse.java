package ru.nsu.fit.core.api.dto;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public record WorkerResponse(UUID requestId, Set<String> data) implements Serializable {
}
