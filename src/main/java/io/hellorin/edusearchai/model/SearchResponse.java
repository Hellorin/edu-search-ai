package io.hellorin.edusearchai.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for search response.
 * This class provides a structured way to handle search responses with proper documentation.
 */
@Schema(description = "Search response containing the answer to the query")
public class SearchResponse {

    @Schema(
        description = "The AI-generated answer based on the search query",
        example = "Based on the documents, the main principles of nutrition include..."
    )
    private String answer;

    @Schema(
        description = "The original query that was searched",
        example = "What are the main principles of nutrition?"
    )
    private String query;

    @Schema(
        description = "Timestamp when the search was performed",
        example = "2024-01-15T10:30:00Z"
    )
    private String timestamp;

    public SearchResponse() {
        // Default constructor for JSON serialization
    }

    public SearchResponse(String answer, String query) {
        this.answer = answer;
        this.query = query;
        this.timestamp = java.time.Instant.now().toString();
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
} 