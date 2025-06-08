package io.hellorin.edusearchai.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import io.hellorin.edusearchai.service.PDFProcessingService;
import io.hellorin.edusearchai.repository.InMemoryDocumentRepository;
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
 *     <li>Storing the processed documents in the document repository</li>
 * </ul>
 */
public class DocumentLoader implements CommandLineRunner {
    private final PDFProcessingService pdfProcessingService;
    private final InMemoryDocumentRepository documentRepository;
    private final ResourcePatternResolver resolver;

    public DocumentLoader(PDFProcessingService pdfProcessingService, 
                         InMemoryDocumentRepository documentRepository) {
        this.pdfProcessingService = pdfProcessingService;
        this.documentRepository = documentRepository;
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
     * Spring Boot CommandLineRunner implementation that executes during application startup.
     * This method:
     * <ul>
     *     <li>Scans both private and public document folders</li>
     *     <li>Processes all found PDF documents</li>
     *     <li>Reports the total number of documents loaded</li>
     * </ul>
     *
     * @param args Command line arguments (not used)
     * @throws Exception if there are any issues during the loading process
     */
    @Override
    public void run(String... args) throws Exception {
        try {
            List<String> folders = List.of("privatedocuments", "publicdocuments"); // Add more folders as needed
            List<Document> allProcessedDocs = new ArrayList<>();
            
            for (String folder : folders) {
                try {
                    List<Document> folderDocs = loadFolder(folder);
                    allProcessedDocs.addAll(folderDocs);
                } catch (IOException e) {
                    System.err.println("Error loading documents from " + folder + ": " + e.getMessage());
                }
            }
            
            // Print repository status
            System.out.println("\nRepository Status:");
            System.out.println("Total documents in repository: " + 
                documentRepository.size());
            
        } catch (Exception e) {
            System.err.println("Error in document loading process: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 