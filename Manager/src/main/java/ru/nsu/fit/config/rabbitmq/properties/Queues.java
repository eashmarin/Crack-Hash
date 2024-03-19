package ru.nsu.fit.config.rabbitmq.properties;

import java.util.List;

public record Queues(
        QueueInfo manager,
        List<QueueInfo> workers
) {
}
