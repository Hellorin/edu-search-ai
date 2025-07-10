package io.hellorin.edusearchai.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.mongodb.atlas.MongoDBAtlasVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class VectorStoreConfig {

    private final String courseCollectionName;

    private final String courseIndexName;

    private final String coursePath;

    private final String noteCollectionName;

    private final String noteIndexName;

    private final String notePath;

    public VectorStoreConfig(
            @Value("${spring.ai.vectorstore.mongodb.course.collection-name}") String courseCollectionName,
            @Value("${spring.ai.vectorstore.mongodb.course.index-name}") String courseIndexName,
            @Value("${spring.ai.vectorstore.mongodb.course.path-name}") String coursePath,
            @Value("${spring.ai.vectorstore.mongodb.note.collection-name}") String noteCollectionName,
            @Value("${spring.ai.vectorstore.mongodb.note.index-name}") String noteIndexName,
            @Value("${spring.ai.vectorstore.mongodb.note.path-name}") String notePath) {
        this.courseCollectionName = courseCollectionName;
        this.courseIndexName = courseIndexName;
        this.coursePath = coursePath;
        this.noteCollectionName = noteCollectionName;
        this.noteIndexName = noteIndexName;
        this.notePath = notePath;
    }

    @Bean
    public VectorStore courseVectorStore(MongoTemplate mongoTemplate, EmbeddingModel embeddingModel) {
        return MongoDBAtlasVectorStore.builder(mongoTemplate, embeddingModel)
                .collectionName(courseCollectionName)
                .vectorIndexName(courseIndexName)
                .pathName(coursePath)
                .initializeSchema(false)
                .build();
    }

    @Bean
    public VectorStore noteVectorStore(MongoTemplate mongoTemplate, EmbeddingModel embeddingModel) {
        return MongoDBAtlasVectorStore.builder(mongoTemplate, embeddingModel)
                .collectionName(noteCollectionName)
                .vectorIndexName(noteIndexName)
                .pathName(notePath)
                .initializeSchema(false)
                .build();
    }


}
