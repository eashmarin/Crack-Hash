package ru.nsu.fit.core.impl.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.nsu.fit.core.impl.domain.Status;

import java.util.Set;
import java.util.UUID;

@Document(collection = "foundWords")
public class FoundWordsDao {
    @Id
    private UUID requestId;
    private Set<String> words;
    private Status status;

    public FoundWordsDao(UUID requestId, Set<String> words, Status status) {
        this.requestId = requestId;
        this.words = words;
        this.status = status;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public Set<String> getWords() {
        return words;
    }

    public void setWords(Set<String> words) {
        this.words = words;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
