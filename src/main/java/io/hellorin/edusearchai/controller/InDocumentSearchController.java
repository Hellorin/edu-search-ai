package io.hellorin.edusearchai.controller;

import io.hellorin.edusearchai.model.SearchRequest;
import io.hellorin.edusearchai.model.SearchResponse;
import io.hellorin.edusearchai.service.InDocumentSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for handling document search operations.
 * This controller provides endpoints for searching and retrieving information from documents.
 */
@RestController
@RequestMapping("/api/search")
@Tag(name = "Document Search", description = "APIs for searching and querying educational documents")
public class InDocumentSearchController {

    private final InDocumentSearchService inDocumentSearchService;

    @Autowired
    public InDocumentSearchController(InDocumentSearchService inDocumentSearchService) {
        this.inDocumentSearchService = inDocumentSearchService;
    }

    /**
     * Endpoint to search within documents and get answers based on the provided query.
     *
     * @param searchRequest The search request containing the query
     * @return ResponseEntity containing either:
     *         - The answer to the query if successful
     *         - A bad request response if the query is empty or null
     */
    @PostMapping("/query")
    @Operation(
        summary = "Search documents and get AI-generated answers",
        description = "Searches through all available educational documents and returns an AI-generated answer based on the provided query."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved answer",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SearchResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - query is empty or null",
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(example = "Query cannot be empty")
            )
        )
    })
    public ResponseEntity<?> searchInDocuments(
            @Parameter(
                description = "Search request containing the query",
                required = true
            )
            @RequestBody SearchRequest searchRequest) {
        if (!searchRequest.isValid()) {
            return ResponseEntity.badRequest().body("Query cannot be empty");
        }

        String answer = inDocumentSearchService.searchAndAnswer(searchRequest.getQuery());
        var response = new SearchResponse(answer, searchRequest.getQuery());
        return ResponseEntity.ok(response);
    }
} 