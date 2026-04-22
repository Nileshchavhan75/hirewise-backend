package com.hirewise.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hirewise.entity.ApprovalStatus;
import com.hirewise.entity.JobListing;
import com.hirewise.entity.JobType;
import com.hirewise.entity.Sector;
import com.hirewise.entity.User;

@Repository
public interface JobListingRepository extends JpaRepository<JobListing, Integer> {

    // Find all active jobs
    List<JobListing> findByIsActiveTrue();

    // ✅ NEW: Find active AND approved jobs with pagination
    Page<JobListing> findByIsActiveTrueAndApprovalStatus(ApprovalStatus status, Pageable pageable);

    // ✅ NEW: Find by approval status
    List<JobListing> findByApprovalStatus(ApprovalStatus status);

    // Find by sector
    List<JobListing> findBySector(Sector sector);

    // Find by job type
    List<JobListing> findByJobType(JobType jobType);

    // Find by location (case insensitive)
    List<JobListing> findByLocationContainingIgnoreCase(String location);

    // Find jobs posted by specific employer
    List<JobListing> findByPostedBy(User postedBy);

    // Custom search query
    @Query("SELECT j FROM JobListing j WHERE " +
           "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:sector IS NULL OR j.sector = :sector) AND " +
           "(:jobType IS NULL OR j.jobType = :jobType) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "j.isActive = true")
    List<JobListing> searchJobs(@Param("keyword") String keyword,
                               @Param("sector") Sector sector,
                               @Param("jobType") JobType jobType,
                               @Param("location") String location);

    // Count active jobs
    long countByIsActiveTrue();
}