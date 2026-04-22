package com.hirewise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hirewise.dto.ApiResponse;
import com.hirewise.dto.UpdateProfileRequest;
import com.hirewise.dto.UserProfileDTO;
import com.hirewise.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
// @CrossOrigin removed - WebConfig.java handles CORS globally
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get user profile by ID
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getUserProfile(@PathVariable Integer userId) {
        UserProfileDTO profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "User profile fetched successfully", profile)
        );
    }

    /**
     * Get user profile by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getUserProfileByEmail(@PathVariable String email) {
        UserProfileDTO profile = userService.getUserProfileByEmail(email);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "User profile fetched successfully", profile)
        );
    }

    /**
     * Update user profile
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateProfile(
            @PathVariable Integer userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileDTO updatedProfile = userService.updateProfile(userId, request);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Profile updated successfully", updatedProfile)
        );
    }

    /**
     * Check if user exists
     */
    @GetMapping("/{userId}/exists")
    public ResponseEntity<ApiResponse<Boolean>> userExists(@PathVariable Integer userId) {
        boolean exists = userService.userExists(userId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "User existence checked", exists)
        );
    }
}