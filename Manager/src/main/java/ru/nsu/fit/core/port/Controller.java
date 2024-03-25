package ru.nsu.fit.core.port;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.nsu.fit.core.api.dto.ManagerCrackRequest;
import ru.nsu.fit.core.api.dto.RequestStatus;
import ru.nsu.fit.core.impl.service.ManagerService;

import java.util.UUID;

@RestController
public class Controller {

    private final ManagerService managerService;

    public Controller(ManagerService managerService) {
        this.managerService = managerService;
    }

    @PostMapping(ManagerUrl.CRACK_REQUEST)
    public UUID crack(@RequestBody ManagerCrackRequest crackRequestBody) {
        return managerService.splitTask(crackRequestBody);
    }

    @GetMapping(ManagerUrl.CRACK_STATUS)
    public ResponseEntity<RequestStatus> checkStatus(@RequestParam("requestId") UUID requestId) {
        RequestStatus taskStatus;
        try {
            taskStatus = managerService.getTaskStatus(requestId);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
        return ResponseEntity.ok(taskStatus);
    }
}
