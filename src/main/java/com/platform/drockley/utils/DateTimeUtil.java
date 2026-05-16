package com.platform.drockley.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class DateTimeUtil {
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(ISO_FORMATTER) : null;
    }
    
    public static LocalDateTime parseDateTime(String dateTimeString) {
        return dateTimeString != null ? LocalDateTime.parse(dateTimeString, ISO_FORMATTER) : null;
    }
    
    public static boolean isBeforeNow(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isBefore(LocalDateTime.now());
    }
    
    public static boolean isAfterNow(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now());
    }
    
    public static boolean isBetween(LocalDateTime checkDate, LocalDateTime startDate, LocalDateTime endDate) {
        return checkDate != null && startDate != null && endDate != null &&
                !checkDate.isBefore(startDate) && !checkDate.isAfter(endDate);
    }
    
    public static long getMinutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0;
        return java.time.temporal.ChronoUnit.MINUTES.between(start, end);
    }
    
    public static long getHoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0;
        return java.time.temporal.ChronoUnit.HOURS.between(start, end);
    }
    
    public static TimeZone getTimeZone(String timeZoneId) {
        try {
            return TimeZone.getTimeZone(timeZoneId != null ? timeZoneId : "UTC");
        } catch (Exception e) {
            return TimeZone.getTimeZone("UTC");
        }
    }
}
