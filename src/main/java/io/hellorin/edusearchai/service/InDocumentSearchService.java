package io.hellorin.edusearchai.service;

import io.hellorin.edusearchai.model.Document;
import io.hellorin.edusearchai.repository.InMemoryDocumentRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InDocumentSearchService {

    private final ChatModel chatModel;
    private final OpenAIEmbeddingService embeddingService;
    private final InMemoryDocumentRepository documentRepository;

    @Autowired
    public InDocumentSearchService(ChatModel chatModel,
                                   OpenAIEmbeddingService embeddingService,
                                   InMemoryDocumentRepository documentRepository) {
        this.chatModel = chatModel;
        this.embeddingService = embeddingService;
        this.documentRepository = documentRepository;
    }

    public String searchAndAnswer(String query) {
        // Get query embedding
        List<Float> queryEmbedding = embeddingService.generateEmbedding(query);
        
        // Find most relevant documents
        List<Document> relevantDocs = documentRepository.findSimilarDocuments(queryEmbedding, 3);
        
        // Prepare context from relevant documents
        String context = relevantDocs.stream()
                .map(doc -> String.format("Title: %s\nSource: %s\nContent: %s", 
                    doc.getTitle(), doc.getSource(), doc.getContent()))
                .collect(Collectors.joining("\n\n"));
        
        // Create prompt template
        PromptTemplate promptTemplate = new PromptTemplate("""
            You are a helpful assistant that answers questions based on the provided context.
            Use only the information from the context to answer the question.
            If the answer cannot be found in the context, say so.
            Include the source of the information in your response when relevant.
            
            Context:
            {context}
            
            Question: {query}
            
            Answer:""");
        
        // Create prompt with context and query
        Prompt prompt = promptTemplate.create(Map.of(
            "context", context,
            "query", query
        ));
        
        // Get response from AI
        return ChatClient.builder(chatModel).build().prompt(prompt)
                .call().content();
    }
} 