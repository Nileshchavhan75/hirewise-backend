package com.hirewise.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hirewise.dto.ResumeSubmissionRequest;
import com.hirewise.entity.ResumeSubmission;
import com.hirewise.entity.SubmissionStatus;
import com.hirewise.exception.ResourceNotFoundException;
import com.hirewise.repository.ResumeSubmissionRepository;

@Service
public class ResumeSubmissionService {

    @Autowired
    private ResumeSubmissionRepository submissionRepository;

    /**
     * Submit resume (Public)
     */
    @Transactional
    public ResumeSubmission submitResume(ResumeSubmissionRequest request) {
        ResumeSubmission submission = new ResumeSubmission();
        submission.setFullName(request.getFullName());
        submission.setEmail(request.getEmail());
        submission.setPhone(request.getPhone());
        submission.setJobInterest(request.getJobInterest());
        submission.setCurrentRole(request.getCurrentRole());
        submission.setExperienceYears(request.getExperienceYears());
        submission.setResumeUrl(request.getResumeUrl());
        submission.setLocation(request.getLocation());
        submission.setStatus(SubmissionStatus.new_status);
        submission.setSubmittedAt(LocalDateTime.now());

        return submissionRepository.save(submission);
    }

    /**
     * Get all submissions (Admin only)
     */
    public List<ResumeSubmission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    /**
     * Get submission by ID
     */
    public ResumeSubmission getSubmissionById(Integer id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + id));
    }

    /**
     * Update submission status (Admin only)
     */
    @Transactional
    public ResumeSubmission updateSubmissionStatus(Integer id, SubmissionStatus status) {
        ResumeSubmission submission = getSubmissionById(id);
        submission.setStatus(status);
        return submissionRepository.save(submission);
    }

    /**
     * Delete submission (Admin only)
     */
    @Transactional
    public void deleteSubmission(Integer id) {
        ResumeSubmission submission = getSubmissionById(id);
        submissionRepository.delete(submission);
    }

    /**
     * Get new submissions
     */
    public List<ResumeSubmission> getNewSubmissions() {
        return submissionRepository.findByStatusOrderBySubmittedAtDesc(SubmissionStatus.new_status);
    }
}