package com.hirewise.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hirewise.dto.JobPostRequest;
import com.hirewise.dto.JobResponseDTO;
import com.hirewise.dto.JobSearchRequest;
import com.hirewise.dto.PageResponse;
import com.hirewise.entity.ApprovalStatus;
import com.hirewise.entity.JobListing;
import com.hirewise.entity.User;
import com.hirewise.exception.ResourceNotFoundException;
import com.hirewise.exception.UnauthorizedException;
import com.hirewise.repository.ApplicationRepository;
import com.hirewise.repository.JobListingRepository;
import com.hirewise.repository.UserRepository;

@Service
public class JobService {

    @Autowired
    private JobListingRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    /**
     * Post a new job (Employer only)
     */
    @Transactional
    public JobResponseDTO postJob(Integer employerId, JobPostRequest request) {
        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        JobListing job = new JobListing();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setSector(request.getSector());
        job.setSubSector(request.getSubSector());
        job.setExperienceRange(request.getExperienceRange());
        job.setSalaryRange(request.getSalaryRange());
        job.setPostedBy(employer);
        job.setApplicationDeadline(request.getApplicationDeadline());
        job.setVacancyCount(request.getVacancyCount());
        job.setRequirements(request.getRequirements());
        job.setBenefits(request.getBenefits());
        job.setIsActive(true);
        
        // ✅ NEW: Set approval status to pending
        job.setApprovalStatus(ApprovalStatus.pending);

        JobListing savedJob = jobRepository.save(job);
        return convertToDTO(savedJob);
    }

    /**
     * Update existing job (Employer/Admin only)
     */
    @Transactional
    public JobResponseDTO updateJob(Integer jobId, Integer userId, JobPostRequest request) {
        JobListing job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        // Check if user is authorized (poster or admin)
        if (!job.getPostedBy().getId().equals(userId) && !isAdmin(userId)) {
            throw new UnauthorizedException("You are not authorized to update this job");
        }

        // Update fields
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setSector(request.getSector());
        job.setSubSector(request.getSubSector());
        job.setExperienceRange(request.getExperienceRange());
        job.setSalaryRange(request.getSalaryRange());
        job.setApplicationDeadline(request.getApplicationDeadline());
        job.setVacancyCount(request.getVacancyCount());
        job.setRequirements(request.getRequirements());
        job.setBenefits(request.getBenefits());

        JobListing updatedJob = jobRepository.save(job);
        return convertToDTO(updatedJob);
    }

    /**
     * Get all active and approved jobs with pagination
     */
    public PageResponse<JobResponseDTO> getAllActiveJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // ✅ UPDATED: Only show jobs that are active AND approved
        Page<JobListing> jobPage = jobRepository.findByIsActiveTrueAndApprovalStatus(
            ApprovalStatus.approved, pageable);

        List<JobResponseDTO> jobs = jobPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                jobs,
                jobPage.getNumber(),
                jobPage.getSize(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages(),
                jobPage.isLast()
        );
    }

    /**
     * Get job by ID
     */
    public JobResponseDTO getJobById(Integer jobId) {
        JobListing job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        return convertToDTO(job);
    }

    /**
     * Search jobs with filters (only approved jobs)
     */
    public List<JobResponseDTO> searchJobs(JobSearchRequest request) {
        List<JobListing> jobs = jobRepository.searchJobs(
                request.getKeyword(),
                request.getSector(),
                request.getJobType(),
                request.getLocation()
        );

        // ✅ Only include approved jobs
        return jobs.stream()
                .filter(job -> job.getIsActive() && job.getApprovalStatus() == ApprovalStatus.approved)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get jobs posted by specific employer
     */
    public List<JobResponseDTO> getJobsByEmployer(Integer employerId) {
        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        return jobRepository.findByPostedBy(employer).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get pending jobs for admin approval
     */
    public List<JobResponseDTO> getPendingJobs() {
        return jobRepository.findByApprovalStatus(ApprovalStatus.pending).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Approve a job (Admin only)
     */
    @Transactional
    public JobResponseDTO approveJob(Integer jobId, Integer adminId) {
        if (!isAdmin(adminId)) {
            throw new UnauthorizedException("Only admin can approve jobs");
        }
        
        JobListing job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        
        job.setApprovalStatus(ApprovalStatus.approved);
        job.setIsActive(true);
        JobListing approvedJob = jobRepository.save(job);
        return convertToDTO(approvedJob);
    }

    /**
     * Reject a job (Admin only)
     */
    @Transactional
    public JobResponseDTO rejectJob(Integer jobId, Integer adminId) {
        if (!isAdmin(adminId)) {
            throw new UnauthorizedException("Only admin can reject jobs");
        }
        
        JobListing job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        
        job.setApprovalStatus(ApprovalStatus.rejected);
        JobListing rejectedJob = jobRepository.save(job);
        return convertToDTO(rejectedJob);
    }

    /**
     * Delete job (soft delete)
     */
    @Transactional
    public void deleteJob(Integer jobId, Integer userId) {
        JobListing job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        // Check if user is authorized (poster or admin)
        if (!job.getPostedBy().getId().equals(userId) && !isAdmin(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this job");
        }

        job.setIsActive(false); // Soft delete
        jobRepository.save(job);
    }

    /**
     * Convert JobListing entity to DTO
     */
    private JobResponseDTO convertToDTO(JobListing job) {
        JobResponseDTO dto = new JobResponseDTO();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setLocation(job.getLocation());
        dto.setJobType(job.getJobType());
        dto.setSector(job.getSector());
        dto.setSubSector(job.getSubSector());
        dto.setExperienceRange(job.getExperienceRange());
        dto.setSalaryRange(job.getSalaryRange());
        
        // ✅ Add approval status to DTO
        dto.setApprovalStatus(job.getApprovalStatus());

        if (job.getPostedBy() != null) {
            dto.setPostedByName(job.getPostedBy().getEmail()); // Will be replaced with company name later
            dto.setPostedById(job.getPostedBy().getId());
        }

        dto.setCreatedAt(job.getCreatedAt());
        dto.setApplicationDeadline(job.getApplicationDeadline());
        dto.setIsActive(job.getIsActive());
        dto.setVacancyCount(job.getVacancyCount());
        dto.setRequirements(job.getRequirements());
        dto.setBenefits(job.getBenefits());

        // Get application count
        Long appCount = applicationRepository.countByJob(job);
        dto.setApplicationCount(appCount);

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