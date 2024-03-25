package ru.nsu.fit.core.impl.domain;

import ru.nsu.fit.core.impl.data.TaskDao;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@org.springframework.stereotype.Service
public class Manager {

    private final static Duration timeout = Duration.of(60, ChronoUnit.SECONDS);
    private final Map<UUID, Task> tasks = new ConcurrentHashMap<>();

    public void setErrorStatus(UUID requestId) {
        Task task = tasks.get(requestId);
        tasks.put(requestId, task.timeoutExceeded());
    }

    public UUID registerTaskRequest() {
        UUID requestId = UUID.randomUUID();
        tasks.put(requestId, Task.defaultTask());
        return requestId;
    }

    public void mergeWords(UUID requestId, Set<String> foundWords, Predicate<Integer> taskDone)
            throws NoSuchTaskInMemoryException {
        Task task = tasks.get(requestId);
        if (task == null) {
            throw new NoSuchTaskInMemoryException();
        }
        tasks.put(requestId, task.workerCompleted(foundWords, taskDone));
    }

    public void restoreTask(TaskDao taskDao, Set<String> foundWords, Predicate<Integer> taskDone) {
        Task task = Task.defaultTask();
        tasks.put(taskDao.getRequestId(), task.workerCompleted(foundWords, taskDone));
    }

    public Task getTask(UUID requestId) {
        return tasks.get(requestId);
    }
}
