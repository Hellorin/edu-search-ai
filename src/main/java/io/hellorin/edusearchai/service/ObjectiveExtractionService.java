package io.hellorin.edusearchai.service;

import io.hellorin.edusearchai.model.ObjectiveDocument;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Service for extracting individual learning objectives from PDF text.
 * This service processes the full text of a document and extracts each objective
 * as a separate ObjectiveDocument entity using text processing techniques.
 */
@Service
public class ObjectiveExtractionService {
    
    private static final Logger logger = LoggerFactory.getLogger(ObjectiveExtractionService.class);
    
    // Patterns to identify learning objectives
    private static final Pattern OBJECTIVE_PATTERNS = Pattern.compile(
        "(?i)(?:learning objective|objective|goal|outcome|competency|skill|ability|capability|proficiency|mastery|understanding|knowledge|comprehension|application|analysis|synthesis|evaluation)" +
        "[\\s\\-:]*" +
        "([^\\n\\r]+)"
    );
    
    private static final Pattern BULLET_PATTERNS = Pattern.compile(
        "(?m)^[\\s]*[•\\-\\*\\+\\d+\\.]+[\\s]+([^\\n\\r]+)"
    );
    
    private static final Pattern NUMBERED_PATTERNS = Pattern.compile(
        "(?m)^[\\s]*\\d+[\\s\\.]+([^\\n\\r]+)"
    );
    
    /**
     * Extracts individual learning objectives from the given text using text processing.
     * Each objective will be created as a separate ObjectiveDocument.
     *
     * @param fullText The complete text content from the PDF
     * @param sourceFilename The original filename of the PDF
     * @return List of ObjectiveDocument objects, one for each extracted objective
     */
    public List<ObjectiveDocument> extractObjectives(String fullText, String sourceFilename) {
        try {
            List<ObjectiveDocument> objectives = new ArrayList<>();
            
            // Extract objectives using various patterns
            objectives.addAll(extractByPattern(fullText, OBJECTIVE_PATTERNS, sourceFilename, "Objective"));
            objectives.addAll(extractByPattern(fullText, BULLET_PATTERNS, sourceFilename, "Bullet"));
            objectives.addAll(extractByPattern(fullText, NUMBERED_PATTERNS, sourceFilename, "Numbered"));
            
            // If no objectives found with patterns, try to split by sentences
            if (objectives.isEmpty()) {
                objectives.addAll(extractBySentences(fullText, sourceFilename));
            }
            
            // Remove duplicates and clean up
            objectives = removeDuplicates(objectives);
            
            logger.info("Extracted {} objectives from {}", objectives.size(), sourceFilename);
            return objectives;
            
        } catch (Exception e) {
            logger.error("Error extracting objectives from {}: {}", sourceFilename, e.getMessage(), e);
            // Fallback: create a single objective with the full text
            ObjectiveDocument fallbackObjective = new ObjectiveDocument();
            fallbackObjective.setId(UUID.randomUUID().toString());
            fallbackObjective.setTitle("Full content from " + sourceFilename);
            fallbackObjective.setContent(fullText);
            fallbackObjective.setSource(sourceFilename);
            fallbackObjective.setTimestamp(System.currentTimeMillis());
            
            return List.of(fallbackObjective);
        }
    }
    
    private List<ObjectiveDocument> extractByPattern(String text, Pattern pattern, String sourceFilename, String type) {
        List<ObjectiveDocument> objectives = new ArrayList<>();
        var matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            String content = matcher.group(1).trim();
            if (!content.isEmpty() && content.length() > 10) {
                ObjectiveDocument objective = new ObjectiveDocument();
                objective.setId(UUID.randomUUID().toString());
                objective.setTitle(type + " from " + sourceFilename);
                objective.setContent(content);
                objective.setSource(sourceFilename);
                objective.setTimestamp(System.currentTimeMillis());
                
                objectives.add(objective);
                logger.debug("Extracted {} objective: {}", type, content);
            }
        }
        
        return objectives;
    }
    
    private List<ObjectiveDocument> extractBySentences(String text, String sourceFilename) {
        List<ObjectiveDocument> objectives = new ArrayList<>();
        
        // Split by common sentence endings and filter for meaningful content
        String[] sentences = text.split("[.!?]+");
        
        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (trimmed.length() > 20 && trimmed.length() < 500 && 
                !trimmed.toLowerCase().contains("page") && 
                !trimmed.toLowerCase().contains("chapter")) {
                
                ObjectiveDocument objective = new ObjectiveDocument();
                objective.setId(UUID.randomUUID().toString());
                objective.setTitle("Sentence from " + sourceFilename);
                objective.setContent(trimmed);
                objective.setSource(sourceFilename);
                objective.setTimestamp(System.currentTimeMillis());
                
                objectives.add(objective);
            }
        }
        
        return objectives;
    }
    
    private List<ObjectiveDocument> removeDuplicates(List<ObjectiveDocument> objectives) {
        List<ObjectiveDocument> uniqueObjectives = new ArrayList<>();
        List<String> seenContents = new ArrayList<>();
        
        for (ObjectiveDocument objective : objectives) {
            String normalizedContent = objective.getContent().toLowerCase().trim();
            if (!seenContents.contains(normalizedContent)) {
                seenContents.add(normalizedContent);
                uniqueObjectives.add(objective);
            }
        }
        
        return uniqueObjectives;
    }
} 