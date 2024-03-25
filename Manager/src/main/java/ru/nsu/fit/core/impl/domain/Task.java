package ru.nsu.fit.core.impl.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class Task {
    private Status status = Status.IN_PROGRESS;
    private final Set<String> data = Collections.synchronizedSet(new HashSet<>());
    private int completedWorkers = 0;

    private Task() {}

    public static Task defaultTask() {
        return new Task();
    }

    public Task workerCompleted(Set<String> foundWords, Predicate<Integer> taskDone) {
        data.addAll(foundWords);
        completedWorkers++;
        if (taskDone.test(completedWorkers)) {
            status = Status.READY;
        }
        return this;
    }

    public Task timeoutExceeded() {
        status = Status.ERROR;
        return this;
    }

    public Status status() {
        return status;
    }

    public Set<String> data() {
        return Collections.unmodifiableSet(data);
    }

}
