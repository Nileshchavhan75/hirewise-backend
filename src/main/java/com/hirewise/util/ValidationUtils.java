package com.hirewise.util;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_REGEX = "^[0-9]{10}$";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";

    /**
     * Validate email
     */
    public static boolean isValidEmail(String email) {
        return email != null && Pattern.matches(EMAIL_REGEX, email);
    }

    /**
     * Validate phone (10 digits)
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && Pattern.matches(PHONE_REGEX, phone);
    }

    /**
     * Validate password strength
     * At least 6 chars, 1 digit, 1 lowercase, 1 uppercase, 1 special char
     */
    public static boolean isStrongPassword(String password) {
        return password != null && Pattern.matches(PASSWORD_REGEX, password);
    }

    /**
     * Check if string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Sanitize input (remove XSS)
     */
    public static String sanitize(String input) {
        if (input == null) {
			return null;
		}
        return input.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    /**
     * Truncate string to max length
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) {
			return null;
		}
        return str.length() <= maxLength ? str : str.substring(0, maxLength) + "...";
    }
}