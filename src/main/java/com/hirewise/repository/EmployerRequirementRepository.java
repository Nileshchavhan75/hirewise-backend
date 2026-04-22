package com.hirewise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hirewise.entity.EmployerRequirement;
import com.hirewise.entity.RequirementStatus;
import com.hirewise.entity.UrgencyLevel;

@Repository
public interface EmployerRequirementRepository extends JpaRepository<EmployerRequirement, Integer> {

    // Find by status
    List<EmployerRequirement> findByStatus(RequirementStatus status);

    // Find by urgency level
    List<EmployerRequirement> findByUrgencyLevel(UrgencyLevel urgencyLevel);

    // Find by company email
    List<EmployerRequirement> findByContactEmail(String email);

    // Find new and high priority requirements
    List<EmployerRequirement> findByStatusAndUrgencyLevel(RequirementStatus status, UrgencyLevel urgencyLevel);
}