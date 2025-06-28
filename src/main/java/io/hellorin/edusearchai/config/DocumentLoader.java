package io.hellorin.edusearchai.config;

import io.hellorin.edusearchai.repository.InMemoryNotesDocumentRepository;
import io.hellorin.edusearchai.repository.InMemoryCourseObjectivesDocumentRepository;
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
import java.util.UUID;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

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
    private final InMemoryCourseObjectivesDocumentRepository inMemoryCourseObjectivesDocumentRepository;
    private final ResourcePatternResolver resolver;

    public DocumentLoader(PDFProcessingService pdfProcessingService, 
                          InMemoryDocumentRepository inMemoryDocumentRepository,
                          InMemoryNotesDocumentRepository inMemoryNotesDocumentRepository,
                          InMemoryCourseObjectivesDocumentRepository inMemoryCourseObjectivesDocumentRepository) {
        this.pdfProcessingService = pdfProcessingService;
        this.inMemoryDocumentRepository = inMemoryDocumentRepository;
        this.inMemoryNotesDocumentRepository = inMemoryNotesDocumentRepository;
        this.inMemoryCourseObjectivesDocumentRepository = inMemoryCourseObjectivesDocumentRepository;
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
     * Loads objectives as strings without computing embeddings.
     * This method extracts text from PDF files and creates Document objects
     * with null embeddings for objectives.
     *
     * @param folderPath The path to the folder containing objective PDF documents
     * @return List of Document objects with text content but no embeddings
     * @throws IOException if there are issues reading the files
     */
    List<Document> loadObjectivesAsStrings(String folderPath) throws IOException {
        Resource[] resources = resolver.getResources("classpath:" + folderPath + "/*.pdf");
        List<Document> documents = new ArrayList<>();
        
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename != null) {
                try (PDDocument document = PDDocument.load(resource.getInputStream())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    String fullText = stripper.getText(document);
                    
                    Document doc = new Document();
                    doc.setId(UUID.randomUUID().toString());
                    doc.setTitle(filename);
                    doc.setContent(fullText);
                    doc.setEmbedding(null); // No embeddings for objectives
                    doc.setSource(filename);
                    doc.setTimestamp(System.currentTimeMillis());
                    
                    documents.add(doc);
                    logger.info("Loaded objective as string: {} (ID: {})", filename, doc.getId());
                }
            }
        }
        
        if (!documents.isEmpty()) {
            logger.info("Loaded {} objective documents as strings from {}!", documents.size(), folderPath);
        } else {
            logger.info("No objective PDF documents found in {} folder.", folderPath);
        }
        
        return documents;
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
            
            // Load course objectives documents as strings (without embeddings)
            List<Document> courseObjectivesDocs = loadObjectivesAsStrings("documents/objectives");
            
            // Save documents to appropriate repositories
            if (!standardDocs.isEmpty()) {
                inMemoryDocumentRepository.saveAll(standardDocs);
            }
            if (!notesDocs.isEmpty()) {
                inMemoryNotesDocumentRepository.saveAll(notesDocs);
            }
            if (!courseObjectivesDocs.isEmpty()) {
                inMemoryCourseObjectivesDocumentRepository.saveAll(courseObjectivesDocs);
            }
            
            // Print repository status
            logger.info("\nRepository Status:");
            logger.info("Total standard documents: {}", inMemoryDocumentRepository.size());
            logger.info("Total notes documents: {}", inMemoryNotesDocumentRepository.size());
            logger.info("Total course objectives documents: {}", inMemoryCourseObjectivesDocumentRepository.size());
            
        } catch (Exception e) {
            logger.error("Error in document loading process: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
} 