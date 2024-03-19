package ru.nsu.fit.config.rabbitmq.properties;

public record QueueInfo(
        String name,
        String routingKey,
        String exchange
) {
}
