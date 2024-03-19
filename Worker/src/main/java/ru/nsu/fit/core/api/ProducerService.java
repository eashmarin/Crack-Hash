package ru.nsu.fit.core.api;

import ru.nsu.fit.core.api.dto.WorkerResponse;

public interface ProducerService {
    void sendWordsToManager(WorkerResponse response);
}
