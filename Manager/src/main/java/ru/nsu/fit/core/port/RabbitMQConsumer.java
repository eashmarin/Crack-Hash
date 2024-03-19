package ru.nsu.fit.core.port;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.nsu.fit.core.api.dto.WorkerResponse;
import ru.nsu.fit.core.impl.domain.Manager;

@Component
@EnableRabbit
public class RabbitMQConsumer {

    private final Manager manager;

    public RabbitMQConsumer(Manager manager) {
        this.manager = manager;
    }

    @RabbitListener(queues = "${spring.rabbitmq.own-queue}")
    public void processWorkerResponse(WorkerResponse response) {
        manager.mergeWords(response.requestId(), response.data());
    }
}
