package com.hirewise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hirewise.entity.ResumeSubmission;
import com.hirewise.entity.SubmissionStatus;

@Repository
public interface ResumeSubmissionRepository extends JpaRepository<ResumeSubmission, Integer> {

    // Find by status
    List<ResumeSubmission> findByStatus(SubmissionStatus status);

    // Find by email
    List<ResumeSubmission> findByEmail(String email);

    // Find new submissions
    List<ResumeSubmission> findByStatusOrderBySubmittedAtDesc(SubmissionStatus status);
}