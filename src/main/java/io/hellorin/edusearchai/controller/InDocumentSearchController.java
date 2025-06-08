package io.hellorin.edusearchai.controller;

import io.hellorin.edusearchai.service.InDocumentSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for handling document search operations.
 * This controller provides endpoints for searching and retrieving information from documents.
 */
@RestController
@RequestMapping("/api/search")
public class InDocumentSearchController {

    private final InDocumentSearchService inDocumentSearchService;

    @Autowired
    public InDocumentSearchController(InDocumentSearchService inDocumentSearchService) {
        this.inDocumentSearchService = inDocumentSearchService;
    }

    /**
     * Endpoint to search within documents and get answers based on the provided query.
     *
     * @param query The search query string to look for in the documents
     * @return ResponseEntity containing either:
     *         - The answer to the query if successful
     *         - A bad request response if the query is empty or null
     */
    @PostMapping("/query")
    public ResponseEntity<String> searchInDocuments(@RequestBody String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Query cannot be empty");
        }

        String answer = inDocumentSearchService.searchAndAnswer(query);
        return ResponseEntity.ok(answer);
    }
} 