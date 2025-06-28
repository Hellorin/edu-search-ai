package io.hellorin.edusearchai.controller;

import io.hellorin.edusearchai.service.ObjectivesService;
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
 * REST Controller for handling objectives-related operations.
 * This controller provides endpoints for managing and analyzing course objectives.
 */
@RestController
@RequestMapping("/api/objectives")
@Tag(name = "Course Objectives", description = "APIs for managing and analyzing course objectives")
public class ObjectivesController {

    private final ObjectivesService objectivesService;

    @Autowired
    public ObjectivesController(ObjectivesService objectivesService) {
        this.objectivesService = objectivesService;
    }

    /**
     * Endpoint to perform vector search for each objective point in a specified objective document against course documents.
     *
     * @param objectiveDocumentName The name of the objective document to search
     * @return ResponseEntity containing either:
     *         - The vector search results for each objective point if successful
     *         - A bad request response if the objective document name is empty or null
     */
    @PostMapping("/search")
    @Operation(
        summary = "Search objectives against course documents",
        description = "Performs vector search for each objective point in the specified objective document against all course documents."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved objective search results",
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(example = "Objective 1: Found relevant content in course documents...")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - objective document name is empty or null",
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(example = "Objective document name cannot be empty")
            )
        )
    })
    public ResponseEntity<String> searchObjectivesAgainstCourses(
            @Parameter(
                description = "The name of the objective document to search",
                required = true,
                example = "weekend1.pdf"
            )
            @RequestBody String objectiveDocumentName) {
        if (objectiveDocumentName == null || objectiveDocumentName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Objective document name cannot be empty");
        }

        String results = objectivesService.searchObjectivesAgainstCourses(objectiveDocumentName.trim());
        return ResponseEntity.ok(results);
    }

    /**
     * Endpoint to get a list of available objective documents.
     *
     * @return ResponseEntity containing a list of available objective document names
     */
    @GetMapping("/list")
    @Operation(
        summary = "Get available objective documents",
        description = "Retrieves a list of all available objective documents that can be used for searching."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved list of objective documents",
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(example = "Available Objective Documents:\n\n- weekend1.pdf\n- weekend2.pdf\n- final_exam.pdf")
            )
        )
    })
    public ResponseEntity<String> getAvailableObjectives() {
        var objectives = objectivesService.getAvailableObjectiveDocuments();
        return ResponseEntity.ok(objectives);
    }
} 