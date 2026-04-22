package com.hirewise.util;

public class AppConstants {

    // API Paths
    public static final String BASE_PATH = "/api";
    public static final String AUTH_PATH = BASE_PATH + "/auth";
    public static final String USER_PATH = BASE_PATH + "/users";
    public static final String JOB_PATH = BASE_PATH + "/jobs";
    public static final String APPLICATION_PATH = BASE_PATH + "/applications";

    // Pagination defaults
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_DIR = "asc";
    public static final String DEFAULT_SORT_BY = "id";

    // Roles
    public static final String ROLE_CANDIDATE = "candidate";
    public static final String ROLE_EMPLOYER = "employer";
    public static final String ROLE_ADMIN = "admin";

    // File upload
    public static final String UPLOAD_DIR = "uploads";
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_FILE_TYPES = {".pdf", ".doc", ".docx", ".jpg", ".png"};

    // JWT
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}