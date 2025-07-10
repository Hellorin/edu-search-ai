package io.hellorin.edusearchai.service;

import io.hellorin.edusearchai.model.DocumentChunk;
import io.hellorin.edusearchai.model.ObjectiveDocument;
import io.hellorin.edusearchai.repository.CourseVectorRepository;
import io.hellorin.edusearchai.repository.ObjectiveDocumentRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class ObjectivesService {

    private final ChatModel chatModel;
    private final ObjectiveDocumentRepository objectiveDocumentRepository;
    private final CourseVectorRepository courseVectorRepository;

    public ObjectivesService(ChatModel chatModel,
                             ObjectiveDocumentRepository objectiveDocumentRepository,
                             CourseVectorRepository courseVectorRepository) {
        this.chatModel = chatModel;
        this.objectiveDocumentRepository = objectiveDocumentRepository;
        this.courseVectorRepository = courseVectorRepository;
    }

    /**
     * Performs vector search for each objective point in a specified objective document against course documents.
     * 
     * @param objectiveDocumentName The name of the objective document to search
     * @return A structured response containing each objective point and its answer based on relevant course content
     */
    public String searchObjectivesAgainstCourses(String objectiveDocumentName) {
        // Find the objective document by name
        var objectiveDocs = objectiveDocumentRepository.findBySource(objectiveDocumentName);

        if (objectiveDocs.isEmpty()) {
            return "Objective document '" + objectiveDocumentName + "' not found.";
        }

        // For each objective point, perform vector search and generate an answer
        var results = new StringBuilder();

        results.append("Answers for Objectives in: ").append(objectiveDocumentName).append("\n\n");

        // Parse objectives into individual points
        for (var objectiveDoc: objectiveDocs) {
            var objectivePoints = parseObjectivePoints(objectiveDoc.getContent());

            if (objectivePoints.isEmpty()) {
                return "No objective points found in document '" + objectiveDocumentName + "'.";
            }

            var relevantCourseDocs = courseVectorRepository.similaritySearch(objectiveDoc.getContent()).stream().map(doc -> new DocumentChunk(
                            doc.getId(),
                            (String) doc.getMetadata().get("title"),
                            doc.getText(),
                            null,
                            (String) doc.getMetadata().get("source"),
                            (Long) doc.getMetadata().get("timestamp")
                    )
            ).toList();

            if (relevantCourseDocs.isEmpty()) {
                results.append("No relevant course content found for this objective.\n\n");
            } else {
                // Generate an answer based on the relevant course content
                var answer = generateAnswerForObjective(objectiveDoc.getContent(), relevantCourseDocs);
                results.append("Answer: ").append(answer).append("\n\n");
            }
            results.append("\n");
        }

        return results.toString();
    }

    /**
     * Generates an answer for a specific objective point based on relevant course documents.
     * 
     * @param objectivePoint The objective point to answer
     * @param relevantDocs The relevant course documents found through vector search
     * @return A generated answer based on the course content
     */
    private String generateAnswerForObjective(String objectivePoint, List<DocumentChunk> relevantDocs) {
        String context = relevantDocs.stream()
                .map(doc -> String.format("Title: %s%nSource: %s%nContent: %s",
                    doc.getTitle(), doc.getSource(), doc.getContent()))
                .collect(Collectors.joining("\n\n"));

        PromptTemplate promptTemplate = new PromptTemplate("""
            You are a helpful educational assistant that answers specific objective points based on the provided course context.
            Provide a comprehensive answer that directly addresses the objective point using the course content.
            If the answer cannot be found in the course context, say so clearly.
            
            Course Context:
            {context}
            
            Objective Point: {objectivePoint}
            
            Answer:""");

        Prompt prompt = promptTemplate.create(Map.of(
                "context", context,
                "objectivePoint", objectivePoint
        ));

        return ChatClient.builder(chatModel).build().prompt(prompt)
                .call().content();
    }

    /**
     * Parses objective content into individual objective points using LLM.
     * This method uses the chat model to intelligently extract objective points.
     * 
     * @param objectiveContent The full content of the objective document
     * @return List of individual objective points
     */
    private List<String> parseObjectivePoints(String objectiveContent) {
        PromptTemplate objectiveParserTemplate = new PromptTemplate("""
            You are a helpful assistant that extracts individual objective points from educational content.
            
            Given the following objective content, extract each individual objective point as a separate item.
            Return ONLY a JSON array of strings, where each string is one complete objective point.
            Do not include any explanations or additional text, just the JSON array.
            
            For example, if the content is:
            "Je suis capable de :
            • Définir ce qu'est l'homéostasie
            • Expliquer les mécanismes"
            
            Return: ["Définir ce qu'est l'homéostasie", "Expliquer les mécanismes"]
            
            Objective Content:
            {objectiveContent}
            """);

        Prompt objectiveParserPrompt = objectiveParserTemplate.create(Map.of(
                "objectiveContent", objectiveContent
        ));

        var response = ChatClient.builder(chatModel).build().prompt(objectiveParserPrompt)
                .call().content();
        
        // Parse the JSON response
        return parseJsonArray(response);
    }

    /**
     * Parses a JSON array string into a List of strings.
     * Handles basic JSON array parsing with fallback to manual parsing.
     * 
     * @param jsonArrayString The JSON array string to parse
     * @return List of strings from the JSON array
     */
    private List<String> parseJsonArray(String jsonArrayString) {
        List<String> points = new ArrayList<>();
        
        try {
            // Try to clean and parse the JSON response
            var cleanedJson = jsonArrayString.trim();
            
            // Remove any markdown code blocks if present
            if (cleanedJson.startsWith("```json")) {
                cleanedJson = cleanedJson.substring(7);
            }
            if (cleanedJson.startsWith("```")) {
                cleanedJson = cleanedJson.substring(3);
            }
            if (cleanedJson.endsWith("```")) {
                cleanedJson = cleanedJson.substring(0, cleanedJson.length() - 3);
            }
            
            cleanedJson = cleanedJson.trim();
            
            // Basic JSON array parsing
            if (cleanedJson.startsWith("[") && cleanedJson.endsWith("]")) {
                var content = cleanedJson.substring(1, cleanedJson.length() - 1);
                var items = content.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                
                for (var item : items) {
                    var trimmedItem = item.trim();
                    if (trimmedItem.startsWith("\"") && trimmedItem.endsWith("\"")) {
                        var point = trimmedItem.substring(1, trimmedItem.length() - 1);
                        point = point.replace("\\\"", "\"").replace("\\\\", "\\");
                        points.add(point);
                    }
                }
            }
        } catch (Exception e) {
            // Fallback: if JSON parsing fails, try to extract points manually
            points = fallbackParseObjectivePoints(jsonArrayString);
        }
        
        return points;
    }

    /**
     * Fallback method to parse objective points when JSON parsing fails.
     * 
     * @param content The content to parse
     * @return List of objective points
     */
    private List<String> fallbackParseObjectivePoints(String content) {
        var points = new ArrayList<String>();
        var lines = content.split("\n");
        
        for (var line : lines) {
            line = line.trim();
            
            // Skip empty lines and common prefixes
            if (line.isEmpty() || line.startsWith("```") || line.startsWith("[") || line.startsWith("]")) {
                continue;
            }
            
            // Remove quotes and commas from JSON-like content
            line = line.replaceAll("^\"|\"$", "").replaceAll(",$", "");
            
            if (!line.isEmpty()) {
                points.add(line);
            }
        }
        
        return points;
    }

    /**
     * Gets a list of available objective document names.
     * 
     * @return A formatted string containing all available objective document names
     */
    public String getAvailableObjectiveDocuments() {
        var documents = objectiveDocumentRepository.findAll();
        
        if (documents.isEmpty()) {
            return "No objective documents available.";
        }
        
        var result = new StringBuilder();
        result.append("Available Objective Documents:\n\n");
        
        documents.forEach(doc -> {
            result.append("- ").append(doc.getSource()).append("\n");
        });
        
        return result.toString();
    }
} 