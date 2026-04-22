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
import com.hirewise.dto.ApplicationResponseDTO;
import com.hirewise.dto.ApplyJobRequest;
import com.hirewise.dto.UpdateApplicationStatusRequest;
import com.hirewise.service.ApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    /**
     * Apply for a job (Candidate only)
     */
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<ApplicationResponseDTO>> applyForJob(
            @RequestParam Integer candidateId,
            @Valid @RequestBody ApplyJobRequest request) {
        ApplicationResponseDTO application = applicationService.applyForJob(candidateId, request);
        return new ResponseEntity<>(
            new ApiResponse<>(true, "Application submitted successfully", application),
            HttpStatus.CREATED
        );
    }

    /**
     * Get applications for a candidate
     */
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<ApiResponse<List<ApplicationResponseDTO>>> getCandidateApplications(
            @PathVariable Integer candidateId) {
        List<ApplicationResponseDTO> applications = applicationService.getCandidateApplications(candidateId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Applications fetched successfully", applications)
        );
    }

    /**
     * Get applications for a job (Employer only)
     */
    @GetMapping("/job/{jobId}")
    public ResponseEntity<ApiResponse<List<ApplicationResponseDTO>>> getJobApplications(
            @PathVariable Integer jobId,
            @RequestParam Integer employerId) {
        List<ApplicationResponseDTO> applications = applicationService.getJobApplications(jobId, employerId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Job applications fetched successfully", applications)
        );
    }

    /**
     * Get all applications for an employer
     */
    @GetMapping("/employer/{employerId}")
    public ResponseEntity<ApiResponse<List<ApplicationResponseDTO>>> getEmployerAllApplications(
            @PathVariable Integer employerId) {
        List<ApplicationResponseDTO> applications = applicationService.getEmployerAllApplications(employerId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "All applications fetched successfully", applications)
        );
    }

    /**
     * Update application status
     */
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<ApiResponse<ApplicationResponseDTO>> updateApplicationStatus(
            @PathVariable Integer applicationId,
            @RequestParam Integer employerId,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {
        ApplicationResponseDTO updatedApplication = applicationService.updateApplicationStatus(
                applicationId, employerId, request);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Application status updated successfully", updatedApplication)
        );
    }

    /**
     * Withdraw application (Candidate only)
     */
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<ApiResponse<Void>> withdrawApplication(
            @PathVariable Integer applicationId,
            @RequestParam Integer candidateId) {
        applicationService.withdrawApplication(applicationId, candidateId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Application withdrawn successfully", null)
        );
    }

    /**
     * Check if candidate has applied for a job
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> hasApplied(
            @RequestParam Integer jobId,
            @RequestParam Integer candidateId) {
        boolean hasApplied = applicationService.hasApplied(jobId, candidateId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Check completed", hasApplied)
        );
    }
}