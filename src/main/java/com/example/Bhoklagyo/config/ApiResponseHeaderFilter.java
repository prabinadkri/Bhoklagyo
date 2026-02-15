package com.example.Bhoklagyo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Adds standard API response headers (versioning, request ID, security headers)
 * and puts the correlation/request ID into MDC for structured log tracing.
 * Industry-standard practice for API gateway / reverse-proxy layers.
 */
@Component
public class ApiResponseHeaderFilter extends OncePerRequestFilter {

    private static final String API_VERSION = "1.0";
    private static final String MDC_REQUEST_ID = "requestId";
    private static final String MDC_CLIENT_IP = "clientIp";
    private static final String MDC_METHOD = "httpMethod";
    private static final String MDC_URI = "requestUri";

    @Value("${spring.application.name:Bhoklagyo}")
    private String applicationName;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Generate or propagate correlation ID
        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null || requestId.isBlank()) {
            requestId = java.util.UUID.randomUUID().toString();
        }

        // Put tracing context into MDC (visible in every log line)
        MDC.put(MDC_REQUEST_ID, requestId);
        MDC.put(MDC_CLIENT_IP, getClientIp(request));
        MDC.put(MDC_METHOD, request.getMethod());
        MDC.put(MDC_URI, request.getRequestURI());

        try {
            // API Versioning
            response.setHeader("X-API-Version", API_VERSION);

            // Request traceability
            response.setHeader("X-Request-ID", requestId);

            // Security headers
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setHeader("X-Frame-Options", "DENY");
            response.setHeader("X-XSS-Protection", "1; mode=block");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

            // Service identification
            response.setHeader("X-Powered-By", applicationName);

            filterChain.doFilter(request, response);
        } finally {
            // Always clear MDC to prevent leaking into other threads
            MDC.remove(MDC_REQUEST_ID);
            MDC.remove(MDC_CLIENT_IP);
            MDC.remove(MDC_METHOD);
            MDC.remove(MDC_URI);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            return xff.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isEmpty()) {
            return realIp;
        }
        return request.getRemoteAddr();
    }
}
