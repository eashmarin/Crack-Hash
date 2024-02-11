package ru.nsu.fit;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.dto.WorkerCrackRequest;

@RestController
@RequestMapping("/internal/api/worker")
public class Controller {

    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @PostMapping("/hash/crack/task")
    public void processManagerTask(WorkerCrackRequest request) {
        service.processManagerTask(request.requestId(), request.hash(), request.maxLength(), request.partCount(), request.partNumber());
    }
}
