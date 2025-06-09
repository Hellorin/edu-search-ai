package io.hellorin.edusearchai.config;

import io.hellorin.edusearchai.repository.InMemoryNotesDocumentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import io.hellorin.edusearchai.service.PDFProcessingService;
import io.hellorin.edusearchai.repository.InMemoryDocumentRepository;
import io.hellorin.edusearchai.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A Spring Boot CommandLineRunner implementation that loads PDF documents from specified folders
 * during application startup. This class is responsible for:
 * <ul>
 *     <li>Scanning designated folders for PDF files</li>
 *     <li>Converting found PDFs into MultipartFile objects</li>
 *     <li>Processing the PDFs using PDFProcessingService</li>
 *     <li>Storing the processed documents in the appropriate document repository</li>
 * </ul>
 */
public class DocumentLoader implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DocumentLoader.class);
    private final PDFProcessingService pdfProcessingService;
    private final InMemoryDocumentRepository inMemoryDocumentRepository;
    private final InMemoryNotesDocumentRepository inMemoryNotesDocumentRepository;
    private final ResourcePatternResolver resolver;

    public DocumentLoader(PDFProcessingService pdfProcessingService, 
                          InMemoryDocumentRepository inMemoryDocumentRepository,
                          InMemoryNotesDocumentRepository inMemoryNotesDocumentRepository) {
        this.pdfProcessingService = pdfProcessingService;
        this.inMemoryDocumentRepository = inMemoryDocumentRepository;
        this.inMemoryNotesDocumentRepository = inMemoryNotesDocumentRepository;
        this.resolver = new PathMatchingResourcePatternResolver();
    }

    /**
     * Loads all PDF documents from a specified folder path.
     *
     * @param folderPath The path to the folder containing PDF documents
     * @return List of processed Document objects
     * @throws IOException if there are issues reading the files
     */
    List<Document> loadFolder(String folderPath) throws IOException {
        Resource[] resources = resolver.getResources("classpath:" + folderPath + "/*.pdf");
        List<MultipartFile> files = new ArrayList<>();
        
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename != null) {
                byte[] content = resource.getInputStream().readAllBytes();
                MultipartFile file = new MockMultipartFile(
                    filename,
                    filename,
                    "application/pdf",
                    content
                );
                files.add(file);
            }
        }
        
        if (!files.isEmpty()) {
            logger.info("Loading {} PDF documents from {}...", files.size(), folderPath);
            List<Document> processedDocs = pdfProcessingService.processPDFs(files);
            logger.info("Documents loaded successfully from {}!", folderPath);
            
            // Print document information
            for (Document doc : processedDocs) {
                logger.info("Loaded document: {} (ID: {})", doc.getSource(), doc.getId());
            }
            return processedDocs;
        } else {
            logger.info("No PDF documents found in {} folder.", folderPath);
            return new ArrayList<>();
        }
    }

    /**
     * Executes during application startup to load and process PDF documents.
     * Loads documents from public, private, and protected folders, processes them,
     * and stores them in their respective repositories.
     *
     * @param args Command line arguments (not used)
     * @throws Exception if there are any issues during the loading process
     */
    @Override
    public void run(String... args) throws Exception {
        try {
            // Load public and courses documents
            List<String> standardFolders = List.of("documents/public", "documents/courses");
            List<Document> standardDocs = new ArrayList<>();
            
            for (String folder : standardFolders) {
                List<Document> folderDocs = loadFolder(folder);
                standardDocs.addAll(folderDocs);
            }
            
            // Load notes documents
            List<Document> notesDocs = loadFolder("documents/notes");
            
            // Save documents to appropriate repositories
            if (!standardDocs.isEmpty()) {
                inMemoryDocumentRepository.saveAll(standardDocs);
            }
            if (!notesDocs.isEmpty()) {
                inMemoryNotesDocumentRepository.saveAll(notesDocs);
            }
            
            // Print repository status
            logger.info("\nRepository Status:");
            logger.info("Total standard documents: {}", inMemoryDocumentRepository.size());
            logger.info("Total notes documents: {}", inMemoryNotesDocumentRepository.size());
            
        } catch (Exception e) {
            logger.error("Error in document loading process: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
} 