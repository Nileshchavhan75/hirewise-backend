package com.hirewise.controller;

import com.hirewise.dto.ApiResponse;
import com.hirewise.entity.ApprovalStatus;
import com.hirewise.entity.User;
import com.hirewise.entity.UserProfile;
import com.hirewise.repository.UserRepository;
import com.hirewise.repository.UserProfileRepository;
import com.hirewise.repository.JobListingRepository;
import com.hirewise.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private JobListingRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    /**
     * Get admin dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalUsers = userRepository.count();
        long totalCandidates = userRepository.findByRole(com.hirewise.entity.Role.candidate).size();
        long totalEmployers = userRepository.findByRole(com.hirewise.entity.Role.employer).size();
        long totalJobs = jobRepository.count();
        long activeJobs = jobRepository.countByIsActiveTrue();
        long totalApplications = applicationRepository.count();
        long pendingJobs = jobRepository.findByApprovalStatus(ApprovalStatus.pending).size();
        
        stats.put("totalUsers", totalUsers);
        stats.put("totalCandidates", totalCandidates);
        stats.put("totalEmployers", totalEmployers);
        stats.put("totalJobs", totalJobs);
        stats.put("activeJobs", activeJobs);
        stats.put("totalApplications", totalApplications);
        stats.put("pendingJobs", pendingJobs);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Stats fetched successfully", stats));
    }

    /**
     * Get all users (with pagination) - UPDATED with full profile data
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        
        List<Map<String, Object>> userList = users.stream().map(user -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", user.getEmail());
            userMap.put("role", user.getRole());
            userMap.put("isActive", user.getIsActive());
            userMap.put("createdAt", user.getCreatedAt());
            
            // Get profile info using UserProfileRepository
            UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);
            if (profile != null) {
                userMap.put("name", profile.getFullName());
                userMap.put("fullName", profile.getFullName());  // ✅ Added
                userMap.put("phone", profile.getPhone());        // ✅ Added
                userMap.put("location", profile.getLocation());  // ✅ Added
                userMap.put("bio", profile.getBio());            // ✅ Added
                userMap.put("resumeUrl", profile.getResumeUrl()); // ✅ Added
                userMap.put("companyName", profile.getCompanyName());
                userMap.put("profilePictureUrl", profile.getProfilePictureUrl());
            } else {
                userMap.put("name", user.getEmail());
                userMap.put("fullName", user.getEmail());
                userMap.put("phone", null);
                userMap.put("location", null);
                userMap.put("bio", null);
                userMap.put("resumeUrl", null);
                userMap.put("companyName", null);
                userMap.put("profilePictureUrl", null);
            }
            
            return userMap;
        }).toList();
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Users fetched successfully", userList));
    }

    /**
     * Block/Activate user
     */
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Integer userId,
            @RequestParam boolean active) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsActive(active);
        userRepository.save(user);
        
        String message = active ? "User activated successfully" : "User blocked successfully";
        return ResponseEntity.ok(new ApiResponse<>(true, message, null));
    }

    /**
     * Delete user
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Integer userId) {
        userRepository.deleteById(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
    }

    /**
     * Get all applications (admin view)
     */
    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllApplications() {
        List<com.hirewise.entity.Application> applications = applicationRepository.findAll();
        
        List<Map<String, Object>> appList = applications.stream().map(app -> {
            Map<String, Object> appMap = new HashMap<>();
            appMap.put("id", app.getId());
            appMap.put("jobId", app.getJob().getId());
            appMap.put("jobTitle", app.getJob().getTitle());
            appMap.put("candidateId", app.getCandidate().getId());
            
            // Get candidate name from UserProfile
            UserProfile candidateProfile = userProfileRepository.findByUserId(app.getCandidate().getId()).orElse(null);
            appMap.put("candidateName", candidateProfile != null ? candidateProfile.getFullName() : app.getCandidate().getEmail());
            appMap.put("candidateEmail", app.getCandidate().getEmail());
            
            appMap.put("employerId", app.getJob().getPostedBy().getId());
            // Get employer name from UserProfile
            UserProfile employerProfile = userProfileRepository.findByUserId(app.getJob().getPostedBy().getId()).orElse(null);
            appMap.put("employerName", employerProfile != null ? employerProfile.getFullName() : app.getJob().getPostedBy().getEmail());
            
            appMap.put("status", app.getStatus());
            appMap.put("appliedAt", app.getAppliedAt());
            appMap.put("resumeUrl", app.getResumeUrl());
            return appMap;
        }).toList();
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Applications fetched successfully", appList));
    }

    // ✅ NEW: Get all jobs with their approval status (for admin)
    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllJobsWithStatus() {
        List<com.hirewise.entity.JobListing> jobs = jobRepository.findAll();
        
        List<Map<String, Object>> jobList = jobs.stream().map(job -> {
            Map<String, Object> jobMap = new HashMap<>();
            jobMap.put("id", job.getId());
            jobMap.put("title", job.getTitle());
            jobMap.put("location", job.getLocation());
            jobMap.put("jobType", job.getJobType());
            jobMap.put("sector", job.getSector());
            jobMap.put("isActive", job.getIsActive());
            jobMap.put("approvalStatus", job.getApprovalStatus());
            jobMap.put("createdAt", job.getCreatedAt());
            
            // Get employer name
            UserProfile employerProfile = userProfileRepository.findByUserId(job.getPostedBy().getId()).orElse(null);
            jobMap.put("employerName", employerProfile != null ? employerProfile.getFullName() : job.getPostedBy().getEmail());
            
            return jobMap;
        }).toList();
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Jobs fetched successfully", jobList));
    }

    // ✅ NEW: Approve or reject a job
    @PutMapping("/jobs/{jobId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveJob(
            @PathVariable Integer jobId,
            @RequestParam ApprovalStatus status) {
        
        com.hirewise.entity.JobListing job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        job.setApprovalStatus(status);
        
        // If approved, make it active
        if (status == ApprovalStatus.approved) {
            job.setIsActive(true);
        }
        // If rejected, deactivate it
        if (status == ApprovalStatus.rejected) {
            job.setIsActive(false);
        }
        
        jobRepository.save(job);
        
        String message = status == ApprovalStatus.approved ? "Job approved successfully" : "Job rejected successfully";
        return ResponseEntity.ok(new ApiResponse<>(true, message, null));
    }

    /**
     * Get pending jobs (for admin approval list)
     */
    @GetMapping("/jobs/pending")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPendingJobs() {
        List<com.hirewise.entity.JobListing> pendingJobs = jobRepository.findByApprovalStatus(ApprovalStatus.pending);
        
        List<Map<String, Object>> jobList = pendingJobs.stream().map(job -> {
            Map<String, Object> jobMap = new HashMap<>();
            jobMap.put("id", job.getId());
            jobMap.put("title", job.getTitle());
            jobMap.put("description", job.getDescription());
            jobMap.put("location", job.getLocation());
            jobMap.put("jobType", job.getJobType());
            jobMap.put("sector", job.getSector());
            jobMap.put("experienceRange", job.getExperienceRange());
            jobMap.put("salaryRange", job.getSalaryRange());
            jobMap.put("vacancyCount", job.getVacancyCount());
            jobMap.put("createdAt", job.getCreatedAt());
            
            // Get employer name
            UserProfile employerProfile = userProfileRepository.findByUserId(job.getPostedBy().getId()).orElse(null);
            jobMap.put("employerName", employerProfile != null ? employerProfile.getFullName() : job.getPostedBy().getEmail());
            jobMap.put("employerEmail", job.getPostedBy().getEmail());
            
            return jobMap;
        }).toList();
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Pending jobs fetched successfully", jobList));
    }
}