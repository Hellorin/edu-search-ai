package io.hellorin.edusearchai.service;

import io.hellorin.edusearchai.model.DocumentChunk;
import io.hellorin.edusearchai.repository.CourseVectorRepository;
import io.hellorin.edusearchai.repository.NoteVectorRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InDocumentSearchService {

    private final ChatModel chatModel;
    private final CourseVectorRepository courseVectorRepository;
    private final NoteVectorRepository noteVectorRepository;

    @Autowired
    public InDocumentSearchService(ChatModel chatModel,
                                   CourseVectorRepository courseVectorRepository,
                                   NoteVectorRepository noteVectorRepository) {
        this.chatModel = chatModel;
        this.courseVectorRepository = courseVectorRepository;
        this.noteVectorRepository = noteVectorRepository;
    }

    public String searchAndAnswer(String query) {
        // Find most relevant documents
        List<Document> documents = courseVectorRepository.similaritySearch(query);

        List<DocumentChunk> relevantDocs = documents.stream().map(doc -> new DocumentChunk(
                doc.getId(),
                (String) doc.getMetadata().get("title"),
                doc.getText(),
                null,
                (String) doc.getMetadata().get("source"),
                (Long) doc.getMetadata().get("timestamp")
                )
        ).toList();
        
        // Get course content response
        String courseContent = getCourseContentResponse(query, relevantDocs);

        // Check if it's a sorry message
        if (checkIfNoSorryMessage(courseContent)) {
            // Find most relevant note documents
            List<DocumentChunk> relevantNoteDocs = noteVectorRepository.similaritySearch(query).stream().map(doc -> new DocumentChunk(
                            doc.getId(),
                            (String) doc.getMetadata().get("title"),
                            doc.getText(),
                            null,
                            (String) doc.getMetadata().get("source"),
                            (Long) doc.getMetadata().get("timestamp")
                    )
            ).toList();
            
            // Get sidenotes response
            String sidenotesContent = getSidenotesResponse(courseContent, relevantNoteDocs);
            
            String combinedResponse = courseContent + "\n\n" + sidenotesContent;
            
            return combinedResponse;
        } else {
            return courseContent;
        }
    }

    private String getCourseContentResponse(String query, List<DocumentChunk> relevantDocs) {
        String context = relevantDocs.stream()
                .map(doc -> String.format("Title: %s%nSource: %s%nContent: %s",
                    doc.getTitle(), doc.getSource(), doc.getContent()))
                .collect(Collectors.joining("\n\n"));

        PromptTemplate promptTemplate = new PromptTemplate("""
            You are a helpful educational assistant that answers questions based on the provided course context.
            Always mention your sources at the end.
            If the answer cannot be found in the course context, say so and say a sorry message.
            
            Course Context:
            {context}
            
            Question: {query}
            
            Answer:""");

        Prompt prompt = promptTemplate.create(Map.of(
                "context", context,
                "query", query
        ));

        return ChatClient.builder(chatModel).build().prompt(prompt)
                .call().content();
    }

    private boolean checkIfNoSorryMessage(String message) {
        PromptTemplate isItASorryPromptTemplate = new PromptTemplate("""
            You are a helpful AI assistant that can determine if a message is a sorry message.
            If it is a sorry message, output <SORRY>.
            If it is not, output <OK>
            
            Message:
            {message}
            """);

        Prompt sorryPrompt = isItASorryPromptTemplate.create(Map.of(
                "message", message
        ));

        var response = Optional.ofNullable(ChatClient.builder(chatModel).build().prompt(sorryPrompt)
                .call().content());
                
        return response.map(r -> !r.contains("<SORRY>")).orElse(false);
    }

    private String getSidenotesResponse(String courseContent, List<DocumentChunk> relevantNoteDocs) {
        String sidenotes = relevantNoteDocs.stream()
                .map(doc -> String.format("Title: %s%nSource: %s%nContent: %s",
                        doc.getTitle(), doc.getSource(), doc.getContent()))
                .collect(Collectors.joining("\n\n"));

        PromptTemplate sideNotesPromptTemplate = new PromptTemplate("""
                You are a helpful educational assistant that answers questions based on the provided course context answer and the sidenotes that was written by the student.
                Start your answer by adding a title sidenotes in the language of the course context answer.
                
                Sidenotes:
                {sidenotes}
                
                Course context answer: {query}
                
                Answer:""");

        Prompt sideNotesprompt = sideNotesPromptTemplate.create(Map.of(
                "sidenotes", sidenotes,
                "query", courseContent
        ));

        return ChatClient.builder(chatModel).build().prompt(sideNotesprompt)
                .call().content();
    }
} 