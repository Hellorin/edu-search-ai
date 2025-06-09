package io.hellorin.edusearchai.repository;

import io.hellorin.edusearchai.model.Document;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of a repository specifically for notes documents.
 * This repository maintains two concurrent maps:
 * 1. documentsById: Maps document IDs to their corresponding Document objects
 * 2. documentsBySource: Maps source identifiers to lists of documents from that source
 */
@Repository
public class InMemoryNotesDocumentRepository {
    
    private final Map<String, Document> documentsById = new ConcurrentHashMap<>();
    private final Map<String, List<Document>> documentsBySource = new ConcurrentHashMap<>();
    
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
    public List<Document> saveAll(List<Document> documents) {
        return documents.stream().map(this::save).toList();
    }

    /**
     * Returns the total number of documents in the repository.
     * @return The size of the repository
     */
    public int size() {
        return documentsById.size();
    }

    /**
     * Finds documents similar to the query embedding using cosine similarity.
     * @param queryEmbedding The embedding vector to compare against
     * @param limit Maximum number of similar documents to return
     * @return List of documents sorted by similarity (most similar first)
     */
    public List<Document> findSimilarDocuments(List<Float> queryEmbedding, int limit) {
        return documentsById.values().stream()
                .filter(doc -> doc.getEmbedding() != null && !doc.getEmbedding().isEmpty())
                .map(doc -> Map.entry(doc, cosineSimilarity(queryEmbedding, doc.getEmbedding())))
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    /**
     * Calculates the cosine similarity between two vectors.
     * Cosine similarity measures the cosine of the angle between two vectors,
     * ranging from -1 to 1, where 1 indicates identical vectors.
     * 
     * @param vec1 First vector
     * @param vec2 Second vector
     * @return Cosine similarity score between the vectors
     * @throws IllegalArgumentException if vectors have different dimensions
     */
    private double cosineSimilarity(List<Float> vec1, List<Float> vec2) {
        if (vec1.size() != vec2.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += vec1.get(i) * vec1.get(i);
            norm2 += vec2.get(i) * vec2.get(i);
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
} 