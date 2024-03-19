package ru.nsu.fit.core.port;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.nsu.fit.core.api.dto.ManagerCrackRequest;
import ru.nsu.fit.core.api.dto.RequestStatus;
import ru.nsu.fit.core.impl.domain.Manager;

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
    public ResponseEntity<RequestStatus> checkStatus(@RequestParam("requestId") UUID requestId) {
        RequestStatus taskStatus;
        try {
            taskStatus = manager.getTaskStatus(requestId);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
        return ResponseEntity.ok(taskStatus);
    }
}
