package ru.nsu.fit.core.impl.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.nsu.fit.config.rabbitmq.properties.QueueInfo;
import ru.nsu.fit.config.rabbitmq.properties.RabbitMQProperties;
import ru.nsu.fit.core.api.ProducerService;
import ru.nsu.fit.core.api.dto.WorkerCrackRequest;

@Service
public class RabbitMQProducerService implements ProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitConfig;

    public RabbitMQProducerService(RabbitTemplate rabbitTemplate, RabbitMQProperties rabbitConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitConfig = rabbitConfig;
    }

    @Override
    public void sendCrackRequestToWorker(WorkerCrackRequest crackRequest, Integer workerNumber) {
        QueueInfo workerQueue = rabbitConfig.getQueues()
                .workers()
                .get(workerNumber - 1);
        rabbitTemplate.convertAndSend(workerQueue.exchange(), workerQueue.routingKey(), crackRequest);
        System.out.println("Sending message: $^_^$ " + crackRequest.requestId());
    }
}
