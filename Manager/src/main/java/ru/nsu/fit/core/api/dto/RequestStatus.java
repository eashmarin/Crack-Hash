package ru.nsu.fit.core.api.dto;

import ru.nsu.fit.core.impl.domain.Status;

public record RequestStatus(Status status, String[] data) {
}
