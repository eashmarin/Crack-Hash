package ru.nsu.fit.core.impl.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FoundWordsRepository extends MongoRepository<FoundWordsDao, UUID> {
}
