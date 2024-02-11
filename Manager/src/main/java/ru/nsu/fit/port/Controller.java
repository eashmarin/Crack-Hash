package ru.nsu.fit.port;

import ru.nsu.fit.Service;
import ru.nsu.fit.dto.ManagerCrackRequest;
import ru.nsu.fit.dto.RequestStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/hash")
public class Controller {

    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @PostMapping("/crack")
    public UUID crack(@RequestBody ManagerCrackRequest crackRequestBody) {
        return service.splitTask(crackRequestBody.hash(), crackRequestBody.maxLength());
    }

    @GetMapping("/status")
    public RequestStatus checkStatus(@RequestParam("requestId") UUID requestId) {
        return service.getTaskStatus(requestId);
    }
}
