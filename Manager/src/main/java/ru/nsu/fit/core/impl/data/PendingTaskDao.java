package ru.nsu.fit.core.impl.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "pendingTasks")
public class PendingTaskDao {
    @Id
    private UUID requestId;
    private String hash;
    private Integer maxLength;

    public PendingTaskDao(UUID requestId, String hash, Integer maxLength) {
        this.requestId = requestId;
        this.hash = hash;
        this.maxLength = maxLength;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public String getHash() {
        return hash;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }
}
