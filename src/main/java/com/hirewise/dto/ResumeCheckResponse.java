package com.hirewise.dto;

import java.util.List;

public class ResumeCheckResponse {
    private boolean success;
    private int score;
    private List<String> matchedKeywords;
    private String jobTitle;
    private String suggestion;

    public ResumeCheckResponse() {}

    public ResumeCheckResponse(boolean success, int score, List<String> matchedKeywords, String jobTitle, String suggestion) {
        this.success = success;
        this.score = score;
        this.matchedKeywords = matchedKeywords;
        this.jobTitle = jobTitle;
        this.suggestion = suggestion;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public List<String> getMatchedKeywords() { return matchedKeywords; }
    public void setMatchedKeywords(List<String> matchedKeywords) { this.matchedKeywords = matchedKeywords; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
}