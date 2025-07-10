package io.hellorin.edusearchai.repository;

import io.hellorin.edusearchai.model.ObjectiveDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ObjectiveDocumentRepository extends MongoRepository<ObjectiveDocument, String> {
    
    /**
     * Find objective document by source (filename)
     */
    List<ObjectiveDocument> findBySource(String source);
    
    /**
     * Find objective document by title
     */
    Optional<ObjectiveDocument> findByTitle(String title);
    
    /**
     * Find all objective documents
     */
    List<ObjectiveDocument> findAll();
    
    /**
     * Delete all objective documents
     */
    void deleteAll();
} 