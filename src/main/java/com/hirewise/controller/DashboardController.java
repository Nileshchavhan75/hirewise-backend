package com.hirewise.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hirewise.dto.ApiResponse;
import com.hirewise.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
//@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Get admin dashboard statistics
     */
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminDashboard() {
        Map<String, Object> stats = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Admin dashboard data fetched successfully", stats)
        );
    }

    /**
     * Get employer dashboard
     */
    @GetMapping("/employer/{employerId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEmployerDashboard(@PathVariable Integer employerId) {
        Map<String, Object> stats = dashboardService.getEmployerDashboard(employerId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Employer dashboard data fetched successfully", stats)
        );
    }

    /**
     * Get candidate dashboard
     */
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCandidateDashboard(@PathVariable Integer candidateId) {
        Map<String, Object> stats = dashboardService.getCandidateDashboard(candidateId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Candidate dashboard data fetched successfully", stats)
        );
    }
}