package io.hellorin.edusearchai.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import io.hellorin.edusearchai.service.PDFProcessingService;
import io.hellorin.edusearchai.repository.InMemoryDocumentRepository;
import io.hellorin.edusearchai.repository.InMemoryNotesDocumentRepository;
import io.hellorin.edusearchai.model.Document;
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
    private final PDFProcessingService pdfProcessingService;
    private final InMemoryDocumentRepository documentRepository;
    private final InMemoryNotesDocumentRepository inMemoryNotesDocumentRepository;
    private final ResourcePatternResolver resolver;

    public DocumentLoader(PDFProcessingService pdfProcessingService, 
                         InMemoryDocumentRepository documentRepository,
                          InMemoryNotesDocumentRepository inMemoryNotesDocumentRepository) {
        this.pdfProcessingService = pdfProcessingService;
        this.documentRepository = documentRepository;
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
            System.out.println("Loading " + files.size() + " PDF documents from " + folderPath + "...");
            List<Document> processedDocs = pdfProcessingService.processPDFs(files);
            System.out.println("Documents loaded successfully from " + folderPath + "!");
            
            // Print document information
            for (Document doc : processedDocs) {
                System.out.println("Loaded document: " + doc.getSource() + 
                                 " (ID: " + doc.getId() + ")");
            }
            return processedDocs;
        } else {
            System.out.println("No PDF documents found in " + folderPath + " folder.");
            return new ArrayList<>();
        }
    }

    /**
     * Executes during application startup to load and process PDF documents.
     * Loads documents from public, courses, and notes folders, processes them,
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
                try {
                    List<Document> folderDocs = loadFolder(folder);
                    standardDocs.addAll(folderDocs);
                } catch (IOException e) {
                    System.err.println("Error loading documents from " + folder + ": " + e.getMessage());
                }
            }

            // Save documents to appropriate repositories
            if (!standardDocs.isEmpty()) {
                documentRepository.saveAll(standardDocs);
            }

            // Load notes documents
            List<Document> notesDocs = new ArrayList<>();
            try {
                notesDocs = loadFolder("documents/notes");
            } catch (IOException e) {
                System.err.println("Error loading notes documents: " + e.getMessage());
            }

            if (!notesDocs.isEmpty()) {
                inMemoryNotesDocumentRepository.saveAll(notesDocs);
            }
            
            // Print repository status
            System.out.println("\nRepository Status:");
            System.out.println("Total standard documents: " + documentRepository.size());
            System.out.println("Total notes documents: " + inMemoryNotesDocumentRepository.size());
            
        } catch (Exception e) {
            System.err.println("Error in document loading process: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 