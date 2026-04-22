package com.hirewise.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hirewise.dto.EmployerRequirementRequest;
import com.hirewise.entity.EmployerRequirement;
import com.hirewise.entity.RequirementStatus;
import com.hirewise.exception.ResourceNotFoundException;
import com.hirewise.repository.EmployerRequirementRepository;

@Service
public class EmployerRequirementService {

    @Autowired
    private EmployerRequirementRepository requirementRepository;

    /**
     * Submit new employer requirement (Public)
     */
    @Transactional
    public EmployerRequirement submitRequirement(EmployerRequirementRequest request) {
        EmployerRequirement requirement = new EmployerRequirement();
        requirement.setCompanyName(request.getCompanyName());
        requirement.setContactPerson(request.getContactPerson());
        requirement.setContactEmail(request.getContactEmail());
        requirement.setContactPhone(request.getContactPhone());
        requirement.setJobTitle(request.getJobTitle());
        requirement.setSector(request.getSector());
        requirement.setJobType(request.getJobType());
        requirement.setDescription(request.getDescription());
        requirement.setLocationPreference(request.getLocationPreference());
        requirement.setExperienceRequired(request.getExperienceRequired());
        requirement.setUrgencyLevel(request.getUrgencyLevel());
        requirement.setStatus(RequirementStatus.new_status);
        requirement.setSubmittedAt(LocalDateTime.now());

        return requirementRepository.save(requirement);
    }

    /**
     * Get all requirements (Admin only)
     */
    public List<EmployerRequirement> getAllRequirements() {
        return requirementRepository.findAll();
    }

    /**
     * Get requirement by ID
     */
    public EmployerRequirement getRequirementById(Integer id) {
        return requirementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found with id: " + id));
    }

    /**
     * Update requirement status (Admin only)
     */
    @Transactional
    public EmployerRequirement updateRequirementStatus(Integer id, RequirementStatus status) {
        EmployerRequirement requirement = getRequirementById(id);
        requirement.setStatus(status);
        return requirementRepository.save(requirement);
    }

    /**
     * Delete requirement (Admin only)
     */
    @Transactional
    public void deleteRequirement(Integer id) {
        EmployerRequirement requirement = getRequirementById(id);
        requirementRepository.delete(requirement);
    }

    /**
     * Get new requirements (Admin only)
     */
    public List<EmployerRequirement> getNewRequirements() {
        return requirementRepository.findByStatus(RequirementStatus.new_status);
    }
}