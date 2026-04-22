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
import com.hirewise.dto.JobPostRequest;
import com.hirewise.dto.JobResponseDTO;
import com.hirewise.dto.JobSearchRequest;
import com.hirewise.dto.PageResponse;
import com.hirewise.service.JobService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class JobController {

    @Autowired
    private JobService jobService;

    /**
     * Post a new job (Employer only)
     * Note: In real app, get employerId from JWT token
     */
    @PostMapping
    public ResponseEntity<ApiResponse<JobResponseDTO>> postJob(
            @RequestParam Integer employerId,
            @Valid @RequestBody JobPostRequest request) {
        JobResponseDTO job = jobService.postJob(employerId, request);
        return new ResponseEntity<>(
            new ApiResponse<>(true, "Job posted successfully", job),
            HttpStatus.CREATED
        );
    }

    /**
     * Update existing job
     */
    @PutMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobResponseDTO>> updateJob(
            @PathVariable Integer jobId,
            @RequestParam Integer userId,
            @Valid @RequestBody JobPostRequest request) {
        JobResponseDTO updatedJob = jobService.updateJob(jobId, userId, request);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Job updated successfully", updatedJob)
        );
    }

    /**
     * Get all active jobs with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<JobResponseDTO>>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<JobResponseDTO> jobs = jobService.getAllActiveJobs(page, size);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Jobs fetched successfully", jobs)
        );
    }

    /**
     * Get job by ID
     */
    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobResponseDTO>> getJobById(@PathVariable Integer jobId) {
        JobResponseDTO job = jobService.getJobById(jobId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Job fetched successfully", job)
        );
    }

    /**
     * Search jobs with filters
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<JobResponseDTO>>> searchJobs(@RequestBody JobSearchRequest request) {
        List<JobResponseDTO> jobs = jobService.searchJobs(request);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Search completed successfully", jobs)
        );
    }

    /**
     * Get jobs by employer
     */
    @GetMapping("/employer/{employerId}")
    public ResponseEntity<ApiResponse<List<JobResponseDTO>>> getJobsByEmployer(@PathVariable Integer employerId) {
        List<JobResponseDTO> jobs = jobService.getJobsByEmployer(employerId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Employer jobs fetched successfully", jobs)
        );
    }

    /**
     * Delete job (soft delete)
     */
    @DeleteMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @PathVariable Integer jobId,
            @RequestParam Integer userId) {
        jobService.deleteJob(jobId, userId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Job deleted successfully", null)
        );
    }
}