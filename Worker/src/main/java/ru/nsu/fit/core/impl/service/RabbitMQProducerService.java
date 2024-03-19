package ru.nsu.fit.core.impl.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.nsu.fit.config.rabbitmq.properties.QueueInfo;
import ru.nsu.fit.config.rabbitmq.properties.RabbitMQProperties;
import ru.nsu.fit.core.api.ProducerService;
import ru.nsu.fit.core.api.dto.WorkerResponse;

@Service
public class RabbitMQProducerService implements ProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitConfig;

    public RabbitMQProducerService(RabbitTemplate rabbitTemplate, RabbitMQProperties rabbitConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitConfig = rabbitConfig;
    }

    @Override
    public void sendWordsToManager(WorkerResponse response) {
        QueueInfo managerQueue = rabbitConfig.getQueues().manager();
        rabbitTemplate.convertAndSend(managerQueue.exchange(), managerQueue.routingKey(), response);
    }
}
