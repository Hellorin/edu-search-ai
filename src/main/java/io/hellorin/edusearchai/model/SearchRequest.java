package io.hellorin.edusearchai.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for search request parameters.
 * This class provides a structured way to handle search requests with proper validation.
 */
@Schema(description = "Search request parameters")
public class SearchRequest {

    @Schema(
        description = "The search query to find information in documents",
        example = "What are the main principles of nutrition?",
        required = true
    )
    private String query;

    public SearchRequest() {
        // Default constructor for JSON deserialization
    }

    public SearchRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isValid() {
        return query != null && !query.trim().isEmpty();
    }
} 