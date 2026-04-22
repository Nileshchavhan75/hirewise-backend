package com.hirewise.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hirewise.dto.ApplicationResponseDTO;
import com.hirewise.dto.ApplyJobRequest;
import com.hirewise.dto.UpdateApplicationStatusRequest;
import com.hirewise.entity.Application;
import com.hirewise.entity.ApplicationStatus;
import com.hirewise.entity.JobListing;
import com.hirewise.entity.User;
import com.hirewise.exception.DuplicateApplicationException;
import com.hirewise.exception.ResourceNotFoundException;
import com.hirewise.exception.UnauthorizedException;
import com.hirewise.repository.ApplicationRepository;
import com.hirewise.repository.JobListingRepository;
import com.hirewise.repository.UserProfileRepository;
import com.hirewise.repository.UserRepository;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobListingRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * Apply for a job (Candidate only)
     */
    @Transactional
    public ApplicationResponseDTO applyForJob(Integer candidateId, ApplyJobRequest request) {
        // Check if job exists
        JobListing job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + request.getJobId()));

        // Check if job is active
        if (!job.getIsActive()) {
            throw new IllegalStateException("This job is no longer accepting applications");
        }

        // Check if candidate already applied
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        boolean alreadyApplied = applicationRepository.existsByJobAndCandidate(job, candidate);
        if (alreadyApplied) {
            throw new DuplicateApplicationException("You have already applied for this job");
        }

        // Create application
        Application application = new Application();
        application.setJob(job);
        application.setCandidate(candidate);
        application.setResumeUrl(request.getResumeUrl());
        application.setCoverLetter(request.getCoverLetter());
        application.setStatus(ApplicationStatus.pending);

        Application savedApplication = applicationRepository.save(application);
        return convertToDTO(savedApplication);
    }

    /**
     * Get applications for a candidate
     */
    public List<ApplicationResponseDTO> getCandidateApplications(Integer candidateId) {
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        return applicationRepository.findByCandidate(candidate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get applications for a job (Employer only)
     */
    public List<ApplicationResponseDTO> getJobApplications(Integer jobId, Integer employerId) {
        JobListing job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        // Verify employer owns this job
        if (!job.getPostedBy().getId().equals(employerId) && !isAdmin(employerId)) {
            throw new UnauthorizedException("You are not authorized to view applications for this job");
        }

        return applicationRepository.findByJob(job).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all applications for an employer (all jobs)
     */
    public List<ApplicationResponseDTO> getEmployerAllApplications(Integer employerId) {
        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        return applicationRepository.findByEmployer(employer).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update application status (Employer only)
     */
    @Transactional
    public ApplicationResponseDTO updateApplicationStatus(Integer applicationId, Integer employerId,
                                                          UpdateApplicationStatusRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        // Verify employer owns the job
        if (!application.getJob().getPostedBy().getId().equals(employerId) && !isAdmin(employerId)) {
            throw new UnauthorizedException("You are not authorized to update this application");
        }

        application.setStatus(request.getStatus());
        application.setNotes(request.getNotes());

        Application updatedApplication = applicationRepository.save(application);
        return convertToDTO(updatedApplication);
    }

    /**
     * Withdraw application (Candidate only)
     */
    @Transactional
    public void withdrawApplication(Integer applicationId, Integer candidateId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        // Verify candidate owns this application
        if (!application.getCandidate().getId().equals(candidateId)) {
            throw new UnauthorizedException("You can only withdraw your own applications");
        }

        applicationRepository.delete(application);
    }

    /**
     * Check if candidate has applied for a job
     */
    public boolean hasApplied(Integer jobId, Integer candidateId) {
        JobListing job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        return applicationRepository.existsByJobAndCandidate(job, candidate);
    }

    /**
     * Convert Application entity to DTO
     */
    private ApplicationResponseDTO convertToDTO(Application application) {
        ApplicationResponseDTO dto = new ApplicationResponseDTO();
        dto.setId(application.getId());
        dto.setJobId(application.getJob().getId());
        dto.setJobTitle(application.getJob().getTitle());

        // Get company name from employer profile
        User employer = application.getJob().getPostedBy();
        String companyName = userProfileRepository.findByUserId(employer.getId())
                .map(profile -> profile.getCompanyName())
                .orElse(employer.getEmail());
        dto.setCompanyName(companyName);

        dto.setCandidateId(application.getCandidate().getId());

        // Get candidate name from profile
        String candidateName = userProfileRepository.findByUserId(application.getCandidate().getId())
                .map(profile -> profile.getFullName())
                .orElse(application.getCandidate().getEmail());
        dto.setCandidateName(candidateName);

        dto.setResumeUrl(application.getResumeUrl());
        dto.setCoverLetter(application.getCoverLetter());
        dto.setStatus(application.getStatus());
        dto.setAppliedAt(application.getAppliedAt());
        dto.setNotes(application.getNotes());

        return dto;
    }

    /**
     * Check if user is admin
     */
    private boolean isAdmin(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null && user.getRole().toString().equals("admin");
    }
}