package ru.nsu.fit.core.api;

import ru.nsu.fit.core.api.dto.WorkerCrackRequest;

public interface ProducerService {
    void sendCrackRequestToWorker(WorkerCrackRequest crackRequest, Integer workerNumber);
}
