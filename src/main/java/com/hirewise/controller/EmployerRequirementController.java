package com.hirewise.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hirewise.dto.ApiResponse;
import com.hirewise.dto.EmployerRequirementRequest;
import com.hirewise.entity.EmployerRequirement;
import com.hirewise.entity.RequirementStatus;
import com.hirewise.service.EmployerRequirementService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employer-requirements")
//@CrossOrigin(origins = "*")
public class EmployerRequirementController {

    @Autowired
    private EmployerRequirementService requirementService;

    /**
     * Submit new employer requirement (Public)
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<EmployerRequirement>> submitRequirement(
            @Valid @RequestBody EmployerRequirementRequest request) {
        EmployerRequirement requirement = requirementService.submitRequirement(request);
        return new ResponseEntity<>(
            new ApiResponse<>(true, "Requirement submitted successfully", requirement),
            HttpStatus.CREATED
        );
    }

    /**
     * Get all requirements (Admin only)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployerRequirement>>> getAllRequirements() {
        List<EmployerRequirement> requirements = requirementService.getAllRequirements();
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Requirements fetched successfully", requirements)
        );
    }

    /**
     * Get requirement by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployerRequirement>> getRequirementById(@PathVariable Integer id) {
        EmployerRequirement requirement = requirementService.getRequirementById(id);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Requirement fetched successfully", requirement)
        );
    }

    /**
     * Update requirement status (Admin only)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<EmployerRequirement>> updateRequirementStatus(
            @PathVariable Integer id,
            @RequestParam RequirementStatus status) {
        EmployerRequirement requirement = requirementService.updateRequirementStatus(id, status);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Requirement status updated successfully", requirement)
        );
    }

    /**
     * Delete requirement (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRequirement(@PathVariable Integer id) {
        requirementService.deleteRequirement(id);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Requirement deleted successfully", null)
        );
    }

    /**
     * Get new requirements (Admin only)
     */
    @GetMapping("/new")
    public ResponseEntity<ApiResponse<List<EmployerRequirement>>> getNewRequirements() {
        List<EmployerRequirement> requirements = requirementService.getNewRequirements();
        return ResponseEntity.ok(
            new ApiResponse<>(true, "New requirements fetched successfully", requirements)
        );
    }
}