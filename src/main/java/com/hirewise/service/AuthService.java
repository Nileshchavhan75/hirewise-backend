package com.hirewise.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hirewise.dto.LoginRequest;
import com.hirewise.dto.LoginResponse;
import com.hirewise.dto.RegisterRequest;
import com.hirewise.entity.User;
import com.hirewise.entity.UserProfile;
import com.hirewise.exception.ResourceNotFoundException;
import com.hirewise.exception.UnauthorizedException;
import com.hirewise.repository.UserProfileRepository;
import com.hirewise.repository.UserRepository;
import com.hirewise.util.JwtUtils;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Register new user
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        // Create user with encoded password
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); // ENCODED NOW
        user.setRole(request.getRole());
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // Create user profile
        UserProfile profile = new UserProfile();
        profile.setUser(savedUser);
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setLocation(request.getLocation());
        profile.setCompanyName(request.getCompanyName());

        userProfileRepository.save(profile);

        // Generate JWT token
        String token = jwtUtils.generateToken(savedUser.getEmail());

        // Return login response WITH token
        return new LoginResponse(
                token,
                savedUser.getEmail(),
                savedUser.getRole(),
                profile.getFullName(),
                savedUser.getId()
        );
    }

    /**
     * Login user
     */
    public LoginResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        // Check password using encoder
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid password");
        }

        // Check if user is active
        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is deactivated. Please contact admin.");
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Get user profile for name
        String fullName = userProfileRepository.findByUserId(user.getId())
                .map(UserProfile::getFullName)
                .orElse(user.getEmail());

        // Generate JWT token
        String token = jwtUtils.generateToken(user.getEmail());

        // Return login response WITH token
        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole(),
                fullName,
                user.getId()
        );
    }

    /**
     * Validate user credentials (for security)
     */
    public User validateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user != null && passwordEncoder.matches(password, user.getPasswordHash()) && user.getIsActive()) {
            return user;
        }

        return null;
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }
}