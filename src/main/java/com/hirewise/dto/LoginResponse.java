package com.hirewise.dto;

import com.hirewise.entity.Role;

public class LoginResponse {

    private String token;
    private String email;
    private Role role;
    private String fullName;
    private Integer userId;
    private String message;

    // Default constructor
    public LoginResponse() {
    }

    // Constructor with all fields
    public LoginResponse(String token, String email, Role role, String fullName, Integer userId) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
        this.userId = userId;
        this.message = "Login successful";
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}