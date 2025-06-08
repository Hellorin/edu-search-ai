package io.hellorin.edusearchai.service;

import io.hellorin.edusearchai.model.Document;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Service class responsible for generating embeddings using OpenAI's embedding model.
 * This service provides functionality to convert text into vector embeddings and process documents
 * by generating embeddings for their content.
 */
@Service
public class OpenAIEmbeddingService {
    
    private final OpenAiEmbeddingModel embeddingModel;
    
    @Autowired
    public OpenAIEmbeddingService(OpenAiEmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }
    
    /**
     * Generates an embedding vector for the given text using OpenAI's embedding model.
     * @param text The input text to generate embedding for
     * @return List of Float values representing the embedding vector
     */
    public List<Float> generateEmbedding(String text) {
        float[] output = embeddingModel.call(new EmbeddingRequest(List.of(text), null)).getResult().getOutput();
        return IntStream.range(0, output.length)
                .mapToObj(i -> output[i])
                .toList();
    }
    
    /**
     * Processes a document by creating a new Document instance and generating its embedding.
     * @param title The title of the document
     * @param content The content of the document to be embedded
     * @param source The source of the document
     * @return A new Document instance with generated embedding and metadata
     */
    public Document processDocument(String title, String content, String source) {
        Document document = new Document();
        document.setId(UUID.randomUUID().toString());
        document.setTitle(title);
        document.setContent(content);
        document.setEmbedding(generateEmbedding(content));
        document.setSource(source);
        document.setTimestamp(System.currentTimeMillis());
        return document;
    }
} 