package ru.nsu.fit.core.port;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.nsu.fit.core.api.dto.WorkerCrackRequest;
import ru.nsu.fit.core.impl.domain.Worker;

import java.io.IOException;

@Component

public class RabbitMQConsumer {

    private final Worker worker;

    public RabbitMQConsumer(Worker worker) {
        this.worker = worker;
    }

    @RabbitListener(queues = "${spring.broker.own-queue}", containerFactory = "myRabbitListenerContainerFactory")
    public void processManagerTask(WorkerCrackRequest request,
                                   Channel channel,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        worker.processManagerTask(request.requestId(), request.hash(), request.maxLength(), request.partNumber(), request.partCount());
        channel.basicAck(tag, false);
    }
}
