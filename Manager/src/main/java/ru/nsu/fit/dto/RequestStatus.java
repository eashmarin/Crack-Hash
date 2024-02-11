package ru.nsu.fit.dto;

import ru.nsu.fit.Status;

public record RequestStatus(Status status, String[] data) {
}
