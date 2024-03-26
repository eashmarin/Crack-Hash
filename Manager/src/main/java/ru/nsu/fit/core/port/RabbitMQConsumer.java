package ru.nsu.fit.core.port;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.nsu.fit.core.api.dto.WorkerResponse;
import ru.nsu.fit.core.impl.service.ManagerService;

import java.io.IOException;

@Component
public class RabbitMQConsumer {

    private final ManagerService managerService;

    public RabbitMQConsumer(ManagerService managerService) {
        this.managerService = managerService;
    }


    @RabbitListener(queues = "${spring.broker.own-queue}", containerFactory = "myRabbitListenerContainerFactory")
    public void processWorkerResponse(WorkerResponse response,
                                      Channel channel,
                                      @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        managerService.processWorkerResponse(response);
        channel.basicAck(tag, false);
    }
}
