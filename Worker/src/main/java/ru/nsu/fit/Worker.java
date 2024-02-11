package ru.nsu.fit;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.nsu.fit.dto.WorkerCrackRequest;

import java.util.UUID;

@SpringBootApplication
public class Worker {
    public static void main(String[] args) {
        SpringApplication.run(Worker.class, args);
    }

    @Autowired
    private Controller controller;

    @PostConstruct
    public void pos() {
        controller.processManagerTask(new WorkerCrackRequest(UUID.randomUUID(), "900150983cd24fb0d6963f7d28e17f72", 3, 1, 1));
    }
}