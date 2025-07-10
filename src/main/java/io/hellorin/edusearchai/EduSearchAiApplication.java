package io.hellorin.edusearchai;

import org.springframework.ai.vectorstore.mongodb.autoconfigure.MongoDBAtlasVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(exclude = MongoDBAtlasVectorStoreAutoConfiguration.class)
@EnableMongoRepositories
public class EduSearchAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(EduSearchAiApplication.class, args);
    }
} 