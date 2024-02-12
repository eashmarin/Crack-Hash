package ru.nsu.fit.port;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.domain.Manager;
import ru.nsu.fit.dto.WorkerResponse;

@RestController
public class InternalController {

    private final Manager manager;

    public InternalController(Manager manager) {
        this.manager = manager;
    }

    @PatchMapping(ManagerUrl.PROCESS_WORKER_RESPONSE)
    public void processWorkerResponse(@RequestBody WorkerResponse workerResponse) {
        manager.mergeWords(workerResponse.requestId(), workerResponse.data());
    }
}
