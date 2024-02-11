package ru.nsu.fit.port;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.Service;
import ru.nsu.fit.dto.WorkerResponse;

@RestController
@RequestMapping("/internal/api")
public class InternalController {

    private final Service service;

    public InternalController(Service service) {
        this.service = service;
    }

    @PatchMapping("/manager/hash/crack/request")
    public void processWorkerResponse(@RequestBody WorkerResponse workerResponse) {
        service.mergeWords(workerResponse.requestId(), workerResponse.data());
    }
}
