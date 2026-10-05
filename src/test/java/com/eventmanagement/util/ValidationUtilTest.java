package com.eventmanagement.util;

import com.eventmanagement.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {
    @Test void acceptsValidEmail() { assertEquals("person@example.com", ValidationUtil.email("Person@Example.com")); }
    @Test void rejectsShortPassword() { assertThrows(ValidationException.class, () -> ValidationUtil.password("short")); }
    @Test void rejectsNonPositiveCapacity() { assertThrows(ValidationException.class, () -> ValidationUtil.positiveInt("0", "Capacity")); }
    @Test void acceptsZeroPrice() { assertEquals(0, ValidationUtil.nonNegativeMoney("0.00").signum()); }
}
