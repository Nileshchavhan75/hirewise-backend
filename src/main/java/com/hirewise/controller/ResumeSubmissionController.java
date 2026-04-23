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
import com.hirewise.dto.ResumeSubmissionRequest;
import com.hirewise.entity.ResumeSubmission;
import com.hirewise.entity.SubmissionStatus;
import com.hirewise.service.ResumeSubmissionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/resume-submissions")
//@CrossOrigin(origins = "*")
public class ResumeSubmissionController {

    @Autowired
    private ResumeSubmissionService submissionService;

    /**
     * Submit resume (Public)
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ResumeSubmission>> submitResume(
            @Valid @RequestBody ResumeSubmissionRequest request) {
        ResumeSubmission submission = submissionService.submitResume(request);
        return new ResponseEntity<>(
            new ApiResponse<>(true, "Resume submitted successfully", submission),
            HttpStatus.CREATED
        );
    }

    /**
     * Get all submissions (Admin only)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ResumeSubmission>>> getAllSubmissions() {
        List<ResumeSubmission> submissions = submissionService.getAllSubmissions();
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Submissions fetched successfully", submissions)
        );
    }

    /**
     * Get submission by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResumeSubmission>> getSubmissionById(@PathVariable Integer id) {
        ResumeSubmission submission = submissionService.getSubmissionById(id);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Submission fetched successfully", submission)
        );
    }

    /**
     * Update submission status (Admin only)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ResumeSubmission>> updateSubmissionStatus(
            @PathVariable Integer id,
            @RequestParam SubmissionStatus status) {
        ResumeSubmission submission = submissionService.updateSubmissionStatus(id, status);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Submission status updated successfully", submission)
        );
    }

    /**
     * Delete submission (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubmission(@PathVariable Integer id) {
        submissionService.deleteSubmission(id);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Submission deleted successfully", null)
        );
    }

    /**
     * Get new submissions (Admin only)
     */
    @GetMapping("/new")
    public ResponseEntity<ApiResponse<List<ResumeSubmission>>> getNewSubmissions() {
        List<ResumeSubmission> submissions = submissionService.getNewSubmissions();
        return ResponseEntity.ok(
            new ApiResponse<>(true, "New submissions fetched successfully", submissions)
        );
    }
}