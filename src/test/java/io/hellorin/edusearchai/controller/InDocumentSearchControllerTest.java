package io.hellorin.edusearchai.controller;

import io.hellorin.edusearchai.model.SearchRequest;
import io.hellorin.edusearchai.model.SearchResponse;
import io.hellorin.edusearchai.service.InDocumentSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InDocumentSearchControllerTest {

    @Mock
    private InDocumentSearchService inDocumentSearchService;

    @InjectMocks
    private InDocumentSearchController inDocumentSearchController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void searchInDocuments_WithValidQuery_ReturnsSuccessResponse() {
        // Arrange
        var searchRequest = new SearchRequest("test query");
        String expectedAnswer = "This is a test answer";
        when(inDocumentSearchService.searchAndAnswer("test query")).thenReturn(expectedAnswer);

        // Act
        ResponseEntity<?> response = inDocumentSearchController.searchInDocuments(searchRequest);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof SearchResponse);
        var searchResponse = (SearchResponse) response.getBody();
        assertEquals(expectedAnswer, searchResponse.getAnswer());
        assertEquals("test query", searchResponse.getQuery());
        assertNotNull(searchResponse.getTimestamp());
        verify(inDocumentSearchService, times(1)).searchAndAnswer("test query");
    }
} 