package ru.nsu.fit.port;

public interface WorkerUrl {
    String WORKER_RESPONSE = "http://manager:8080/internal/api/manager/hash/crack/request";
    String WORKER_CRACK_REQUEST = "/internal/api/worker/hash/crack/task";
}
