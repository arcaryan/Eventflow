package com.eventmanagement.util;

import com.eventmanagement.exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private ValidationUtil() { }

    public static String required(String value, String label) {
        if (value == null || value.isBlank()) throw new ValidationException(label + " is required.");
        return value.trim();
    }
    public static String email(String value) {
        String email = required(value, "Email").toLowerCase();
        if (!EMAIL.matcher(email).matches()) throw new ValidationException("Enter a valid email address.");
        return email;
    }
    public static String password(String value) {
        if (value == null || value.length() < 8) throw new ValidationException("Password must be at least 8 characters.");
        return value;
    }
    public static int positiveInt(String value, String label) {
        try {
            int number = Integer.parseInt(required(value, label));
            if (number <= 0) throw new NumberFormatException();
            return number;
        } catch (NumberFormatException e) { throw new ValidationException(label + " must be a positive number."); }
    }
    public static BigDecimal nonNegativeMoney(String value) {
        try {
            BigDecimal money = new BigDecimal(required(value, "Price"));
            if (money.signum() < 0) throw new NumberFormatException();
            return money;
        } catch (NumberFormatException e) { throw new ValidationException("Price must be zero or greater."); }
    }
    public static LocalDate date(String value, String label) {
        try { return LocalDate.parse(required(value, label)); }
        catch (DateTimeParseException e) { throw new ValidationException(label + " must be a valid date."); }
    }
    public static LocalTime time(String value, String label) {
        try { return LocalTime.parse(required(value, label)); }
        catch (DateTimeParseException e) { throw new ValidationException(label + " must be a valid time."); }
    }
}
