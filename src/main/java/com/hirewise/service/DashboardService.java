package com.hirewise.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hirewise.dto.JobResponseDTO;
import com.hirewise.entity.ApplicationStatus;
import com.hirewise.entity.JobListing;
import com.hirewise.entity.User;
import com.hirewise.exception.ResourceNotFoundException;
import com.hirewise.repository.ApplicationRepository;
import com.hirewise.repository.EmployerRequirementRepository;
import com.hirewise.repository.JobListingRepository;
import com.hirewise.repository.ResumeSubmissionRepository;
import com.hirewise.repository.UserRepository;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobListingRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private EmployerRequirementRepository requirementRepository;

    @Autowired
    private ResumeSubmissionRepository submissionRepository;

    /**
     * Get admin dashboard statistics
     */
    public Map<String, Object> getAdminDashboard() {
        Map<String, Object> stats = new HashMap<>();

        // User stats
        stats.put("totalUsers", userRepository.count());
        stats.put("totalCandidates", userRepository.findByRole(com.hirewise.entity.Role.candidate).size());
        stats.put("totalEmployers", userRepository.findByRole(com.hirewise.entity.Role.employer).size());

        // Job stats
        stats.put("totalJobs", jobRepository.count());
        stats.put("activeJobs", jobRepository.countByIsActiveTrue());

        // Application stats
        stats.put("totalApplications", applicationRepository.count());

        // New submissions
        stats.put("newEmployerRequirements", requirementRepository.findByStatus(com.hirewise.entity.RequirementStatus.new_status).size());
        stats.put("newResumeSubmissions", submissionRepository.findByStatus(com.hirewise.entity.SubmissionStatus.new_status).size());

        return stats;
    }

    /**
     * Get employer dashboard
     */
    public Map<String, Object> getEmployerDashboard(Integer employerId) {
        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        Map<String, Object> stats = new HashMap<>();

        // Jobs posted by this employer
        List<JobListing> employerJobs = jobRepository.findByPostedBy(employer);
        stats.put("totalJobsPosted", employerJobs.size());
        stats.put("activeJobs", employerJobs.stream().filter(JobListing::getIsActive).count());

        // Applications received
        long totalApplications = employerJobs.stream()
                .mapToLong(job -> applicationRepository.countByJob(job))
                .sum();
        stats.put("totalApplications", totalApplications);

        // Applications by status
        Map<ApplicationStatus, Long> statusCount = new HashMap<>();
        for (ApplicationStatus status : ApplicationStatus.values()) {
            long count = 0;
            for (JobListing job : employerJobs) {
                count += applicationRepository.countByJobAndStatus(job, status);
            }
            statusCount.put(status, count);
        }
        stats.put("applicationsByStatus", statusCount);

        // Recent jobs
        List<JobResponseDTO> recentJobs = employerJobs.stream()
                .limit(5)
                .map(job -> {
                    JobResponseDTO dto = new JobResponseDTO();
                    dto.setId(job.getId());
                    dto.setTitle(job.getTitle());
                    dto.setCreatedAt(job.getCreatedAt());
                    dto.setApplicationCount(applicationRepository.countByJob(job));
                    return dto;
                })
                .collect(Collectors.toList());
        stats.put("recentJobs", recentJobs);

        return stats;
    }

    /**
     * Get candidate dashboard
     */
    public Map<String, Object> getCandidateDashboard(Integer candidateId) {
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        Map<String, Object> stats = new HashMap<>();

        // Applications submitted
        List<com.hirewise.entity.Application> applications = applicationRepository.findByCandidate(candidate);
        stats.put("totalApplications", applications.size());

        // Applications by status
        Map<ApplicationStatus, Long> statusCount = applications.stream()
                .collect(Collectors.groupingBy(
                        com.hirewise.entity.Application::getStatus,
                        Collectors.counting()
                ));
        stats.put("applicationsByStatus", statusCount);

        // Recent applications
        List<Map<String, Object>> recentApplications = applications.stream()
                .limit(5)
                .map(app -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("jobTitle", app.getJob().getTitle());
                    map.put("companyName", app.getJob().getPostedBy().getEmail()); // Will be replaced
                    map.put("status", app.getStatus());
                    map.put("appliedAt", app.getAppliedAt());
                    return map;
                })
                .collect(Collectors.toList());
        stats.put("recentApplications", recentApplications);

        // Available jobs count
        stats.put("availableJobs", jobRepository.countByIsActiveTrue());

        return stats;
    }
}