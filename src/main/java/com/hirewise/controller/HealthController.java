package com.hirewise.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hirewise.dto.ApiResponse;

@RestController
@RequestMapping("/api/health")
//@CrossOrigin(origins = "*")
public class HealthController {

    /**
     * Health check endpoint
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "HireWise Backend");
        status.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(
            new ApiResponse<>(true, "Service is running", status)
        );
    }

    /**
     * Test endpoint
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test() {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "HireWise Backend is working perfectly!", null)
        );
    }
}