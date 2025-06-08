package io.hellorin.edusearchai.controller;

import io.hellorin.edusearchai.service.InDocumentSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        String query = "test query";
        String expectedAnswer = "This is a test answer";
        when(inDocumentSearchService.searchAndAnswer(query)).thenReturn(expectedAnswer);

        // Act
        ResponseEntity<String> response = inDocumentSearchController.searchInDocuments(query);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedAnswer, response.getBody());
        verify(inDocumentSearchService, times(1)).searchAndAnswer(query);
    }

    @Test
    void searchInDocuments_WithEmptyQuery_ReturnsBadRequest() {
        // Arrange
        String query = "";

        // Act
        ResponseEntity<String> response = inDocumentSearchController.searchInDocuments(query);

        // Assert
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Query cannot be empty", response.getBody());
        verify(inDocumentSearchService, never()).searchAndAnswer(anyString());
    }

    @Test
    void searchInDocuments_WithNullQuery_ReturnsBadRequest() {
        // Act
        ResponseEntity<String> response = inDocumentSearchController.searchInDocuments(null);

        // Assert
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Query cannot be empty", response.getBody());
        verify(inDocumentSearchService, never()).searchAndAnswer(anyString());
    }
} 