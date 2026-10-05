package com.eventmanagement.util;

import com.eventmanagement.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class ServletUtil {
    public static final String CURRENT_USER = "currentUser";
    private ServletUtil() { }

    public static User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute(CURRENT_USER);
    }
    public static void flash(HttpServletRequest request, String type, String message) {
        request.getSession().setAttribute("flash_" + type, message);
    }
    public static void exposeFlash(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return;
        for (String type : new String[]{"success", "error", "info"}) {
            Object value = session.getAttribute("flash_" + type);
            if (value != null) {
                request.setAttribute("flash_" + type, value);
                session.removeAttribute("flash_" + type);
            }
        }
    }
}
