package ru.nsu.fit.port;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.domain.Worker;
import ru.nsu.fit.dto.WorkerCrackRequest;

@RestController
public class Controller {

    private final Worker worker;

    public Controller(Worker worker) {
        this.worker = worker;
    }

    @PostMapping(WorkerUrl.WORKER_CRACK_REQUEST)
    public void processManagerTask(@RequestBody WorkerCrackRequest request) {
        worker.processManagerTask(request.requestId(), request.hash(), request.maxLength(), request.partCount(), request.partNumber());
    }
}
