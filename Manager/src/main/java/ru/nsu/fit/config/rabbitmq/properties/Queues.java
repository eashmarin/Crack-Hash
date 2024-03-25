package ru.nsu.fit.config.rabbitmq.properties;

public record Queues(
        QueueInfo manager,
        QueueInfo worker
) {
}
