package ru.nsu.fit.port;

import ru.nsu.fit.domain.Manager;
import ru.nsu.fit.dto.ManagerCrackRequest;
import ru.nsu.fit.dto.RequestStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class Controller {

    private final Manager manager;

    public Controller(Manager manager) {
        this.manager = manager;
    }

    @PostMapping(ManagerUrl.CRACK_REQUEST)
    public UUID crack(@RequestBody ManagerCrackRequest crackRequestBody) {
        return manager.splitTask(crackRequestBody.hash(), crackRequestBody.maxLength());
    }

    @GetMapping(ManagerUrl.CRACK_STATUS)
    public RequestStatus checkStatus(@RequestParam("requestId") UUID requestId) {
        return manager.getTaskStatus(requestId);
    }
}
