package ru.nsu.fit.dto;

import ru.nsu.fit.domain.Status;

public record RequestStatus(Status status, String[] data) {
}
