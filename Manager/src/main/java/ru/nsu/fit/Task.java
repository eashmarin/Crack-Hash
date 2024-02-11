package ru.nsu.fit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Task {
    private Status status = Status.IN_PROGRESS;
    private final Set<String> data = new HashSet<>();
    private int completedWorkers = 0;

    private Task() {}

    public static Task defaultTask() {
        return new Task();
    }

    public Task workerCompleted(Set<String> foundWords) {
        data.addAll(foundWords);
        completedWorkers++;
        if (completedWorkers == Service.WORKERS_NUMBER) {
            status = Status.READY;
        }
        return this;
    }

    public Status status() {
        return status;
    }

    public Set<String> data() {
        return Collections.unmodifiableSet(data);
    }

}
