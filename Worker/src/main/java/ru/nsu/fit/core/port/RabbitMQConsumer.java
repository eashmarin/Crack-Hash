package ru.nsu.fit.core.port;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.nsu.fit.core.api.dto.WorkerCrackRequest;
import ru.nsu.fit.core.impl.domain.Worker;

@Component
@EnableRabbit
public class RabbitMQConsumer {

    private final Worker worker;

    public RabbitMQConsumer(Worker worker) {
        this.worker = worker;
    }

    @RabbitListener(queues = "${spring.rabbitmq.own-queue}")
    public void processManagerTask(WorkerCrackRequest request) {
        worker.processManagerTask(request.requestId(), request.hash(), request.maxLength(), request.partNumber(), request.partCount());
    }
}
