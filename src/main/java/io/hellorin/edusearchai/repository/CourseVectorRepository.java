package io.hellorin.edusearchai.repository;

import io.hellorin.edusearchai.model.DocumentChunk;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repository for managing course document vectors using MongoDB Atlas.
 * This repository handles adding, clearing, and searching course document chunks.
 */
@Repository
public class CourseVectorRepository {

    private final MongoTemplate mongoTemplate;
    private final VectorStore vectorStore;
    private final String collectionName;

    public CourseVectorRepository(MongoTemplate mongoTemplate, @Qualifier("courseVectorStore") VectorStore vectorStore,
                                  @Value("${spring.ai.vectorstore.mongodb.course.collection-name}") String collectionName) {
        this.mongoTemplate = mongoTemplate;
        this.vectorStore = vectorStore;
        this.collectionName = collectionName;
    }

    /**
     * Adds all document chunks to the vector store.
     *
     * @param documentChunk List of document chunks to add
     */
    public void addAll(List<DocumentChunk> documentChunk) {
        vectorStore.add(
                documentChunk.stream()
                        .map(it ->
                                new Document(
                                    it.getContent(),
                                    Map.of(
                                        "title", it.getTitle(),
                                        "source", it.getSource(),
                                        "timestamp", it.getTimestamp()
                                    )
                                )
                        ).toList());
    }

    /**
     * Clears all vectors from the vector store.
     *
     * @throws RuntimeException if clearing fails
     */
    public void clearVectors() {
        try {
            mongoTemplate.remove(new Query(), collectionName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear vector store", e);
        }
    }

    /**
     * Performs similarity search on the vector store.
     *
     * @param query The search query
     * @return List of similar documents
     */
    public List<Document> similaritySearch(String query) {
        return vectorStore.similaritySearch(SearchRequest.builder().query(query).topK(3).build());
    }
}
