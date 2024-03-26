package ru.nsu.fit.core.impl.domain;

import ru.nsu.fit.core.impl.data.FoundWordsDao;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

@org.springframework.stereotype.Service
public class Manager {

    public UUID registerTaskRequest() {
        return UUID.randomUUID();
    }

    public Task mergeWords(Optional<FoundWordsDao> wordsDao, Set<String> foundWords, Predicate<Integer> taskDone) {
        if (wordsDao.isPresent()) {
            Set<String> data = wordsDao.get().getWords();
            data.addAll(foundWords);
            return Task.defaultTask().workerCompleted(data, taskDone);
        }

        return Task.defaultTask().workerCompleted(foundWords, taskDone);
    }
}
