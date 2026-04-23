package com.hirewise.controller;

import com.hirewise.dto.ResumeCheckResponse;
import com.hirewise.entity.JobListing;
import com.hirewise.repository.JobListingRepository;
import com.hirewise.service.ResumeParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resume")
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ResumeCheckerController {

    @Autowired
    private JobListingRepository jobRepository;

    @Autowired
    private ResumeParserService parserService;

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "the", "and", "for", "with", "this", "that", "from", "are", "was", "were",
        "has", "have", "been", "will", "can", "could", "should", "would", "may",
        "might", "must", "a", "an", "of", "to", "in", "is", "on", "at", "by", "be",
        "as", "or", "not", "but", "for", "of", "by", "on", "at", "so", "no", "yes"
    ));

    @PostMapping(value = "/check", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> checkResume(
            @RequestParam("resume") MultipartFile file,
            @RequestParam("jobId") Integer jobId) {

        try {
            // Check if file is empty
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "No file uploaded"));
            }

            // Check file type
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".pdf") && !fileName.endsWith(".docx"))) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Only PDF and DOCX files are allowed"));
            }

            // Check if job exists
            Optional<JobListing> jobOpt = jobRepository.findById(jobId);
            if (jobOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Job not found"));
            }
            
            JobListing job = jobOpt.get();

            // Extract text from resume
            String resumeText = parserService.extractText(file);
            if (resumeText == null || resumeText.trim().length() < 50) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Could not extract enough text. Please use a clearer PDF/DOCX file."));
            }

            // Prepare job text for matching
            String jobText = job.getTitle() + " " +
                    (job.getDescription() != null ? job.getDescription() : "") + " " +
                    (job.getRequirements() != null ? job.getRequirements() : "");

            // Calculate match score
            MatchResult match = calculateMatch(resumeText, jobText);

            // Generate suggestion
            String suggestion;
            if (match.score >= 80) {
                suggestion = "Excellent match! Your resume aligns well with the job requirements.";
            } else if (match.score >= 60) {
                suggestion = "Good match! Consider highlighting the missing keywords in your resume.";
            } else if (match.score >= 40) {
                suggestion = "Average match. Your resume needs improvement for this position.";
            } else {
                suggestion = "Low match. Your resume needs significant tailoring for this position.";
            }

            ResumeCheckResponse response = new ResumeCheckResponse(
                true, 
                match.score, 
                match.matchedKeywords, 
                job.getTitle(), 
                suggestion
            );
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Internal error: " + e.getMessage()));
        }
    }

    private MatchResult calculateMatch(String resumeText, String jobText) {
        // Clean and normalize text
        String cleanResume = resumeText.toLowerCase().replaceAll("[^a-z0-9\\s]", "");
        String cleanJob = jobText.toLowerCase().replaceAll("[^a-z0-9\\s]", "");

        // Extract unique words from resume (excluding stop words)
        Set<String> resumeWords = Arrays.stream(cleanResume.split("\\s+"))
                .filter(w -> w.length() > 2 && !STOP_WORDS.contains(w))
                .collect(Collectors.toSet());

        // Extract job keywords (allowing duplicates for frequency later)
        List<String> jobKeywordList = Arrays.stream(cleanJob.split("\\s+"))
                .filter(w -> w.length() > 2 && !STOP_WORDS.contains(w))
                .collect(Collectors.toList());

        // Unique job keywords for matching
        Set<String> uniqueJobKeywords = new HashSet<>(jobKeywordList);
        
        if (uniqueJobKeywords.isEmpty()) {
            return new MatchResult(0, new ArrayList<>());
        }

        // Find matching keywords
        List<String> matched = uniqueJobKeywords.stream()
                .filter(resumeWords::contains)
                .collect(Collectors.toList());

        // Calculate percentage score
        int score = (int) Math.round((double) matched.size() / uniqueJobKeywords.size() * 100);
        
        return new MatchResult(score, matched);
    }

    private static class MatchResult {
        int score;
        List<String> matchedKeywords;
        
        MatchResult(int score, List<String> matchedKeywords) {
            this.score = score;
            this.matchedKeywords = matchedKeywords;
        }
    }
}