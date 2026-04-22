package com.hirewise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hirewise.entity.Application;
import com.hirewise.entity.ApplicationStatus;
import com.hirewise.entity.JobListing;
import com.hirewise.entity.User;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    // Find by candidate
    List<Application> findByCandidate(User candidate);

    // Find by job
    List<Application> findByJob(JobListing job);

    // Find by status
    List<Application> findByStatus(ApplicationStatus status);

    // Check if already applied
    boolean existsByJobAndCandidate(JobListing job, User candidate);

    // Find applications for a specific job with status
    List<Application> findByJobAndStatus(JobListing job, ApplicationStatus status);

    // Count applications per job
    Long countByJob(JobListing job);

    // Count applications per job by status
    Long countByJobAndStatus(JobListing job, ApplicationStatus status);

    // Get all applications for an employer (jobs posted by employer)
    @Query("SELECT a FROM Application a WHERE a.job.postedBy = :employer")
    List<Application> findByEmployer(@Param("employer") User employer);
}