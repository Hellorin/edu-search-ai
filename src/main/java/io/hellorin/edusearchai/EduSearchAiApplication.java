package io.hellorin.edusearchai;

import io.hellorin.edusearchai.repository.InMemoryNotesDocumentRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import io.hellorin.edusearchai.config.DocumentLoader;
import io.hellorin.edusearchai.service.PDFProcessingService;
import io.hellorin.edusearchai.repository.InMemoryDocumentRepository;

@SpringBootApplication
public class EduSearchAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(EduSearchAiApplication.class, args);
    }

    @Bean
    public DocumentLoader documentLoader(PDFProcessingService pdfProcessingService, 
                                       InMemoryDocumentRepository documentRepository,
                                       InMemoryNotesDocumentRepository inMemoryNotesDocumentRepository) {
        return new DocumentLoader(pdfProcessingService, documentRepository, inMemoryNotesDocumentRepository);
    }
} 