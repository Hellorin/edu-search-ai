package io.hellorin.edusearchai.config;

import io.hellorin.edusearchai.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import io.hellorin.edusearchai.service.PDFProcessingService;
import io.hellorin.edusearchai.service.ObjectiveExtractionService;
import io.hellorin.edusearchai.model.DocumentChunk;
import io.hellorin.edusearchai.model.ObjectiveDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
@Component
public class DocumentLoader implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DocumentLoader.class);

    @Autowired
    private Environment env;

    private final PDFProcessingService pdfProcessingService;
    private final ObjectiveExtractionService objectiveExtractionService;

    private final CourseVectorRepository courseVectorRepository;
    private final NoteVectorRepository noteVectorRepository;
    private final ObjectiveDocumentRepository objectiveDocumentRepository;

    private final ResourcePatternResolver resolver;

    public DocumentLoader(PDFProcessingService pdfProcessingService,
                          ObjectiveExtractionService objectiveExtractionService,
                          CourseVectorRepository courseVectorRepository, NoteVectorRepository noteVectorRepository,
                          ObjectiveDocumentRepository objectiveDocumentRepository) {
        this.pdfProcessingService = pdfProcessingService;
        this.objectiveExtractionService = objectiveExtractionService;
        this.courseVectorRepository = courseVectorRepository;
        this.noteVectorRepository = noteVectorRepository;
        this.objectiveDocumentRepository = objectiveDocumentRepository;
        this.resolver = new PathMatchingResourcePatternResolver();
    }

    /**
     * Loads all PDF documents from a specified folder path.
     *
     * @param folderPath The path to the folder containing PDF documents
     * @return List of processed Document objects
     * @throws IOException if there are issues reading the files
     */
    List<DocumentChunk> loadFolder(String folderPath) throws IOException {
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
            List<DocumentChunk> processedDocs = pdfProcessingService.processPDFs(files);
            logger.info("Documents loaded successfully from {}!", folderPath);
            
            // Print document information
            for (DocumentChunk doc : processedDocs) {
                logger.info("Loaded document: {} (ID: {})", doc.getSource(), doc.getId());
            }
            return processedDocs;
        } else {
            logger.info("No PDF documents found in {} folder.", folderPath);
            return new ArrayList<>();
        }
    }

    /**
     * Loads objectives and extracts individual learning objectives using LLM.
     * This method extracts text from PDF files, uses AI to identify individual objectives,
     * and creates separate ObjectiveDocument objects for each objective.
     *
     * @param folderPath The path to the folder containing objective PDF documents
     * @return List of ObjectiveDocument objects, one for each extracted objective
     * @throws IOException if there are issues reading the files
     */
    List<ObjectiveDocument> loadObjectivesAsStrings(String folderPath) throws IOException {
        Resource[] resources = resolver.getResources("classpath:" + folderPath + "/*.pdf");
        List<ObjectiveDocument> allObjectives = new ArrayList<>();
        
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename != null) {
                try (PDDocument document = PDDocument.load(resource.getInputStream())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    String fullText = stripper.getText(document);
                    
                    // Use LLM to extract individual objectives from the full text
                    List<ObjectiveDocument> extractedObjectives = objectiveExtractionService.extractObjectives(fullText, filename);
                    allObjectives.addAll(extractedObjectives);
                    
                    logger.info("Extracted {} objectives from {}", extractedObjectives.size(), filename);
                }
            }
        }
        
        if (!allObjectives.isEmpty()) {
            logger.info("Extracted {} total objectives from {}!", allObjectives.size(), folderPath);
        } else {
            logger.info("No objectives extracted from {} folder.", folderPath);
        }
        
        return allObjectives;
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
            if (Arrays.asList(env.getActiveProfiles()).contains("loading")) {
                // Load public and courses documents
                List<String> standardFolders = List.of("documents/public", "documents/courses");
                List<DocumentChunk> standardDocs = new ArrayList<>();

                courseVectorRepository.clearVectors();
                for (String folder : standardFolders) {
                    List<DocumentChunk> folderDocs = loadFolder(folder);
                    standardDocs.addAll(folderDocs);
                    courseVectorRepository.addAll(folderDocs);
                }

                // Load notes documents
                noteVectorRepository.clearVectors();
                List<DocumentChunk> notesDocs = loadFolder("documents/notes");
                noteVectorRepository.addAll(notesDocs);

                // Clean up all objectives before loading new ones
                objectiveDocumentRepository.deleteAll();

                // Load course objectives documents and extract individual objectives using LLM
                List<ObjectiveDocument> courseObjectivesDocs = loadObjectivesAsStrings("documents/objectives");

                // Save documents to appropriate repositories
                if (!courseObjectivesDocs.isEmpty()) {
                    objectiveDocumentRepository.saveAll(courseObjectivesDocs);
                }

                // Print repository status
                logger.info("\nRepository Status:");
                logger.info("Total standard documents: {}", standardDocs.size());
                logger.info("Total notes documents: {}", notesDocs.size());
                logger.info("Total extracted objectives: {}", courseObjectivesDocs.size());
                logger.info("Total objectives in repository: {}", objectiveDocumentRepository.count());
            }
            
        } catch (Exception e) {
            logger.error("Error in document loading process: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
} 