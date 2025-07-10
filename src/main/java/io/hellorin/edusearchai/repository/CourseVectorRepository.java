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

    public void addAll(List<DocumentChunk> documentChunk) {
        vectorStore.add(
                documentChunk.stream()
                        .map(it ->
                                new org.springframework.ai.document.Document(
                                    it.getContent(),
                                    Map.of(
                                        "title", it.getTitle(),
                                        "source", it.getSource(),
                                        "timestamp", it.getTimestamp()
                                    )
                                )
                        ).toList());
    }

    public void clearVectors() {
        try {
            mongoTemplate.remove(new Query(), collectionName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear vector store", e);
        }
    }

    public List<Document> similaritySearch(String query) {
        return vectorStore.similaritySearch(SearchRequest.builder().query(query).topK(3).build());
    }
}
