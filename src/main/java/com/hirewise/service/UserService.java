package com.hirewise.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hirewise.dto.UpdateProfileRequest;
import com.hirewise.dto.UserProfileDTO;
import com.hirewise.entity.User;
import com.hirewise.entity.UserProfile;
import com.hirewise.exception.ResourceNotFoundException;
import com.hirewise.repository.UserProfileRepository;
import com.hirewise.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * Get user profile by user ID
     */
    public UserProfileDTO getUserProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(new UserProfile()); // Return empty profile if not exists

        return convertToDTO(user, profile);
    }

    /**
     * Get user profile by email
     */
    public UserProfileDTO getUserProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElse(new UserProfile());

        return convertToDTO(user, profile);
    }

    /**
     * Update user profile
     */
    @Transactional
    public UserProfileDTO updateProfile(Integer userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(new UserProfile());

        // Set user reference if new profile
        if (profile.getUserId() == null) {
            profile.setUser(user);
        }

        // Update fields
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setLocation(request.getLocation());
        profile.setResumeUrl(request.getResumeUrl());
        profile.setCompanyName(request.getCompanyName());
        profile.setProfilePictureUrl(request.getProfilePictureUrl());
        profile.setBio(request.getBio());

        UserProfile savedProfile = userProfileRepository.save(profile);
        return convertToDTO(user, savedProfile);
    }

    /**
     * Convert User and UserProfile to DTO
     */
    private UserProfileDTO convertToDTO(User user, UserProfile profile) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(profile.getFullName());
        dto.setPhone(profile.getPhone());
        dto.setLocation(profile.getLocation());
        dto.setResumeUrl(profile.getResumeUrl());
        dto.setCompanyName(profile.getCompanyName());
        dto.setProfilePictureUrl(profile.getProfilePictureUrl());
        dto.setBio(profile.getBio());
        dto.setRole(user.getRole());
        return dto;
    }

    /**
     * Check if user exists
     */
    public boolean userExists(Integer userId) {
        return userRepository.existsById(userId);
    }

    /**
     * Check if user is employer
     */
    public boolean isEmployer(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getRole().toString().equals("employer");
    }

    /**
     * Check if user is admin
     */
    public boolean isAdmin(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getRole().toString().equals("admin");
    }
}