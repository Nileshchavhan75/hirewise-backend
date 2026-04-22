package com.hirewise.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.hirewise.entity.ApprovalStatus;
import com.hirewise.entity.JobType;
import com.hirewise.entity.Sector;

public class JobResponseDTO {

    private Integer id;
    private String title;
    private String description;
    private String location;
    private JobType jobType;
    private Sector sector;
    private String subSector;
    private String experienceRange;
    private String salaryRange;
    private String postedByName;
    private Integer postedById;
    private LocalDateTime createdAt;
    private LocalDate applicationDeadline;
    private Boolean isActive;
    private Integer vacancyCount;
    private String requirements;
    private String benefits;
    private Long applicationCount;
    
    // ✅ NEW FIELD
    private ApprovalStatus approvalStatus;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public JobType getJobType() { return jobType; }
    public void setJobType(JobType jobType) { this.jobType = jobType; }

    public Sector getSector() { return sector; }
    public void setSector(Sector sector) { this.sector = sector; }

    public String getSubSector() { return subSector; }
    public void setSubSector(String subSector) { this.subSector = subSector; }

    public String getExperienceRange() { return experienceRange; }
    public void setExperienceRange(String experienceRange) { this.experienceRange = experienceRange; }

    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }

    public String getPostedByName() { return postedByName; }
    public void setPostedByName(String postedByName) { this.postedByName = postedByName; }

    public Integer getPostedById() { return postedById; }
    public void setPostedById(Integer postedById) { this.postedById = postedById; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDate getApplicationDeadline() { return applicationDeadline; }
    public void setApplicationDeadline(LocalDate applicationDeadline) { this.applicationDeadline = applicationDeadline; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Integer getVacancyCount() { return vacancyCount; }
    public void setVacancyCount(Integer vacancyCount) { this.vacancyCount = vacancyCount; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }

    public Long getApplicationCount() { return applicationCount; }
    public void setApplicationCount(Long applicationCount) { this.applicationCount = applicationCount; }

    // ✅ NEW GETTER AND SETTER
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
}