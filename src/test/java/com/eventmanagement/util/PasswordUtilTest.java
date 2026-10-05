package com.eventmanagement.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {
    @Test void hashesAndVerifiesPassword() {
        String hash = PasswordUtil.hash("correct-password");
        assertNotEquals("correct-password", hash);
        assertTrue(PasswordUtil.matches("correct-password", hash));
        assertFalse(PasswordUtil.matches("wrong-password", hash));
    }

    @Test void acceptsTheDocumentedDemoHash() {
        assertTrue(PasswordUtil.matches("password", "$2a$10$ZRQtZTNM16etyX/bNmopYulSiQ/L7EgSAFPbhUrevb9kJhen2tjo2"));
    }
}
