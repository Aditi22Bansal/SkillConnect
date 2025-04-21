package com.skillconnect.utils;

import java.util.Date;
import java.util.regex.Pattern;

public class ValidationUtils {
    // Constants for validation limits
    public static final int MAX_SKILLS_PER_USER = 10;
    private static final int MAX_PAGE_SIZE = 100;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

    /**
     * Validates if a phone number is in correct format (10 digits)
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validates if an ID is positive
     */
    public static boolean isValidId(int id) {
        return id > 0;
    }

    /**
     * Validates pagination parameters
     */
    public static boolean isValidPage(int page, int size) {
        return page >= 0 && size > 0 && size <= MAX_PAGE_SIZE;
    }

    /**
     * Validates if the number of skills is within allowed limits
     */
    public static boolean isValidSkillCount(int count) {
        return count > 0 && count <= MAX_SKILLS_PER_USER;
    }

    /**
     * Validates if a date range is logical (start not after end)
     */
    public static boolean isValidDateRange(Date start, Date end) {
        return start != null && end != null && !start.after(end);
    }

    /**
     * Validates if a string can be parsed to a positive integer
     */
    public static boolean isValidPositiveNumber(String number) {
        try {
            return Integer.parseInt(number) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates if a number is within a specified range
     */
    public static boolean isInRange(int number, int min, int max) {
        return number >= min && number <= max;
    }

    /**
     * Validates if a decimal number is positive
     */
    public static boolean isPositiveDecimal(double number) {
        return number > 0.0;
    }
}