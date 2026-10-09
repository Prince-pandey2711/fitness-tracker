package com.college.fitnesstracker.config;

import com.college.fitnesstracker.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // Allow public routes and static assets
        if (uri.startsWith("/login") ||
            uri.startsWith("/register") ||
            uri.startsWith("/css") ||
            uri.startsWith("/js") ||
            uri.startsWith("/images") ||
            uri.startsWith("/h2-console") ||
            uri.equals("/") ||
            uri.startsWith("/error")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect("/login?msg=Please+sign+in+to+continue");
            return false;
        }

        if (uri.startsWith("/admin") && !currentUser.isAdmin()) {
            response.sendRedirect("/user/dashboard?error=Access+denied.+Administrator+privileges+required.");
            return false;
        }

        return true;
    }
}
