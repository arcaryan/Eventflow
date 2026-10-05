package com.eventmanagement.util;

import java.util.UUID;

public final class ReferenceCodeUtil {
    private ReferenceCodeUtil() { }
    public static String next() { return "EVT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase(); }
}
