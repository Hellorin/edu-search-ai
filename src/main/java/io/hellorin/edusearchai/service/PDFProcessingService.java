package io.hellorin.edusearchai.service;

import io.hellorin.edusearchai.model.Document;
import io.hellorin.edusearchai.repository.InMemoryDocumentRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for processing PDF files and converting them into searchable documents.
 * This service handles PDF text extraction, chunking, and embedding generation for document search functionality.
 */
@Service
public class PDFProcessingService {
    
    private final OpenAIEmbeddingService embeddingService;
    private final InMemoryDocumentRepository documentRepository;
    
    public PDFProcessingService(OpenAIEmbeddingService embeddingService,
                              InMemoryDocumentRepository documentRepository) {
        this.embeddingService = embeddingService;
        this.documentRepository = documentRepository;
    }
    
    /**
     * Processes multiple PDF files and converts them into searchable documents.
     * Each PDF is split into chunks of 1500 characters for better search results.
     *
     * @param files List of PDF files to process
     * @return List of processed Document objects
     * @throws IOException if there's an error reading the PDF files
     */
    public List<Document> processPDFs(List<MultipartFile> files) throws IOException {
        List<Document> documents = new ArrayList<>();
        
        for (MultipartFile file : files) {
            // Use chunking for each file
            documents.addAll(processPDFWithChunks(file, 1500));
        }
        
        return documents;
    }
    
    /**
     * Processes a single PDF file by splitting it into chunks and generating embeddings.
     * The text is extracted from the PDF and split into manageable chunks for better search results.
     *
     * @param file The PDF file to process
     * @param chunkSize The maximum size of each text chunk
     * @return List of processed Document objects with embeddings
     * @throws IOException if there's an error reading the PDF file
     */
    public List<Document> processPDFWithChunks(MultipartFile file, int chunkSize) throws IOException {
        try (PDDocument document = PDDocument.load(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String fullText = stripper.getText(document);
            
            // Split text into chunks
            List<String> chunks = splitIntoChunks(fullText, chunkSize);
            List<Document> documents = new ArrayList<>();
            
            for (int i = 0; i < chunks.size(); i++) {
                String chunk = chunks.get(i);
                Document doc = embeddingService.processDocument(
                    file.getOriginalFilename() + " - Chunk " + (i + 1),
                    chunk,
                        file.getOriginalFilename()
                );
                documents.add(doc);
            }
            
            // Save all chunks to repository
            return documentRepository.saveAll(documents);
        }
    }
    
    /**
     * Splits a text into chunks of specified size, trying to break at word boundaries.
     * This method ensures that chunks don't break words in the middle and maintains
     * readability of the text.
     *
     * @param text The text to split into chunks
     * @param chunkSize The maximum size of each chunk
     * @return List of text chunks
     */
    private List<String> splitIntoChunks(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            if (end < text.length()) {
                // Try to find a good breaking point (space or newline)
                while (end > start && !Character.isWhitespace(text.charAt(end - 1))) {
                    end--;
                }
                if (end == start) {
                    // If no good breaking point found, just cut at chunkSize
                    end = Math.min(start + chunkSize, text.length());
                }
            }
            chunks.add(text.substring(start, end).trim());
            start = end;
        }
        return chunks;
    }
} 