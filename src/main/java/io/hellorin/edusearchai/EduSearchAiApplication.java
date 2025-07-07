package io.hellorin.edusearchai;

import com.vaadin.flow.theme.Theme;
import io.hellorin.edusearchai.repository.InMemoryNotesDocumentRepository;
import io.hellorin.edusearchai.repository.InMemoryCourseObjectivesDocumentRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import io.hellorin.edusearchai.config.DocumentLoader;
import io.hellorin.edusearchai.service.PDFProcessingService;
import io.hellorin.edusearchai.repository.InMemoryDocumentRepository;

@SpringBootApplication
@Theme(value = "edusearch")
public class EduSearchAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(EduSearchAiApplication.class, args);
    }

    @Bean
    public DocumentLoader documentLoader(PDFProcessingService pdfProcessingService, 
                                       InMemoryDocumentRepository documentRepository,
                                       InMemoryNotesDocumentRepository inMemoryNotesDocumentRepository,
                                       InMemoryCourseObjectivesDocumentRepository inMemoryCourseObjectivesDocumentRepository) {
        return new DocumentLoader(pdfProcessingService, documentRepository, inMemoryNotesDocumentRepository, inMemoryCourseObjectivesDocumentRepository);
    }
} 