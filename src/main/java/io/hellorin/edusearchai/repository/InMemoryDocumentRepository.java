package io.hellorin.edusearchai.repository;

import io.hellorin.edusearchai.component.MathComponent;
import io.hellorin.edusearchai.model.Document;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of a document repository that stores and retrieves documents.
 * This repository maintains two concurrent maps:
 * 1. documentsById: Maps document IDs to their corresponding Document objects
 * 2. documentsBySource: Maps source identifiers to lists of documents from that source
 */
@Repository
public class InMemoryDocumentRepository extends DocumentRepository {
    
    private final Map<String, Document> documentsById = new ConcurrentHashMap<>();
    private final Map<String, List<Document>> documentsBySource = new ConcurrentHashMap<>();

    public InMemoryDocumentRepository(MathComponent mathComponent) {
        super(mathComponent);
    }

    @Override
    public Map<String, Document> getDocumentsById() {
        return this.documentsById;
    }

    /**
     * Saves a single document to both maps.
     * @param document The document to save
     * @return The saved document
     */
    private Document save(Document document) {
        documentsById.put(document.getId(), document);
        documentsBySource.computeIfAbsent(document.getSource(), k -> new ArrayList<>())
                        .add(document);
        return document;
    }
    
    /**
     * Saves multiple documents to the repository.
     * @param documents List of documents to save
     * @return List of saved documents
     */
    @Override
    public List<Document> saveAll(List<Document> documents) {
        return documents.stream().map(this::save).toList();
    }

    /**
     * Returns the total number of documents in the repository.
     * @return The size of the repository
     */
    @Override
    public int size() {
        return documentsById.size();
    }
} 