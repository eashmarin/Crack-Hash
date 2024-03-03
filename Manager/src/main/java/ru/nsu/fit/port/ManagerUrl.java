package ru.nsu.fit.port;

public interface ManagerUrl {
    String CRACK_REQUEST = "/api/hash/crack";
    String CRACK_STATUS = "/api/hash/status";
    String PROCESS_WORKER_RESPONSE = "/internal/api/manager/hash/crack/request";
    String WORKER_CRACK_REQUEST_TEMPLATE = "http://worker%d:8080/internal/api/worker/hash/crack/task";
}
