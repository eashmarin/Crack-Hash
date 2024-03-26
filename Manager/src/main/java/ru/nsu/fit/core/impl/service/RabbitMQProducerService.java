package ru.nsu.fit.core.impl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
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

    public RabbitMQProducerService(RabbitTemplate rabbitTemplate,
                                   RabbitMQProperties rabbitConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitConfig = rabbitConfig;
        rabbitTemplate.setMandatory(true);
    }

    @Override
    public void sendCrackRequestToWorker(WorkerCrackRequest crackRequest, Integer workerNumber) {
        QueueInfo workerQueue = rabbitConfig.getQueues().worker();
        rabbitTemplate.convertAndSend(workerQueue.exchange(), workerQueue.routingKey(), crackRequest);
    }
}
