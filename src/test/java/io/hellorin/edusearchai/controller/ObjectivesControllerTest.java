package io.hellorin.edusearchai.controller;

import io.hellorin.edusearchai.service.ObjectivesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ObjectivesControllerTest {

    @Mock
    private ObjectivesService objectivesService;

    @InjectMocks
    private ObjectivesController objectivesController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void searchObjectivesAgainstCourses_WithValidDocumentName_ReturnsSuccessResponse() {
        // Arrange
        String objectiveDocumentName = "weekend1.pdf";
        String expectedResult = "Answers for Objectives in: weekend1.pdf\n\n=== Objective Point 1 ===\nObjective: Test objective\n\nAnswer: Test answer\n\n";
        when(objectivesService.searchObjectivesAgainstCourses(objectiveDocumentName)).thenReturn(expectedResult);

        // Act
        ResponseEntity<String> response = objectivesController.searchObjectivesAgainstCourses(objectiveDocumentName);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResult, response.getBody());
        verify(objectivesService, times(1)).searchObjectivesAgainstCourses(objectiveDocumentName);
    }

    @Test
    void searchObjectivesAgainstCourses_WithEmptyDocumentName_ReturnsBadRequest() {
        // Arrange
        String objectiveDocumentName = "";

        // Act
        ResponseEntity<String> response = objectivesController.searchObjectivesAgainstCourses(objectiveDocumentName);

        // Assert
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Objective document name cannot be empty", response.getBody());
        verify(objectivesService, never()).searchObjectivesAgainstCourses(anyString());
    }

    @Test
    void searchObjectivesAgainstCourses_WithNullDocumentName_ReturnsBadRequest() {
        // Act
        ResponseEntity<String> response = objectivesController.searchObjectivesAgainstCourses(null);

        // Assert
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Objective document name cannot be empty", response.getBody());
        verify(objectivesService, never()).searchObjectivesAgainstCourses(anyString());
    }

    @Test
    void getAvailableObjectives_ReturnsSuccessResponse() {
        // Arrange
        String expectedResult = "Available Objective Documents:\n\n- weekend1.pdf\n- weekend2.pdf\n";
        when(objectivesService.getAvailableObjectiveDocuments()).thenReturn(expectedResult);

        // Act
        ResponseEntity<String> response = objectivesController.getAvailableObjectives();

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResult, response.getBody());
        verify(objectivesService, times(1)).getAvailableObjectiveDocuments();
    }

    @Test
    void generateCourseObjectivesResponse_WithValidCourseContent_ReturnsSuccessResponse() {
        // Arrange
        String courseContent = "This is test course content";
        String relevantObjectivesDocs = "weekend1.pdf,weekend2.pdf";

        // Act
        ResponseEntity<String> response = objectivesController.generateCourseObjectivesResponse(courseContent, relevantObjectivesDocs);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Course objectives generation endpoint - implementation pending", response.getBody());
    }

    @Test
    void generateCourseObjectivesResponse_WithEmptyCourseContent_ReturnsBadRequest() {
        // Arrange
        String courseContent = "";
        String relevantObjectivesDocs = "weekend1.pdf";

        // Act
        ResponseEntity<String> response = objectivesController.generateCourseObjectivesResponse(courseContent, relevantObjectivesDocs);

        // Assert
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Course content cannot be empty", response.getBody());
    }

    @Test
    void generateCourseObjectivesResponse_WithNullCourseContent_ReturnsBadRequest() {
        // Arrange
        String relevantObjectivesDocs = "weekend1.pdf";

        // Act
        ResponseEntity<String> response = objectivesController.generateCourseObjectivesResponse(null, relevantObjectivesDocs);

        // Assert
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Course content cannot be empty", response.getBody());
    }
} 