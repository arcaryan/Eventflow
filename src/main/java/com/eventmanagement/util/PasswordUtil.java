package com.eventmanagement.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
    private PasswordUtil() { }
    public static String hash(String rawPassword) { return BCrypt.hashpw(rawPassword, BCrypt.gensalt(12)); }
    public static boolean matches(String rawPassword, String hash) {
        return rawPassword != null && hash != null && BCrypt.checkpw(rawPassword, hash);
    }
}
