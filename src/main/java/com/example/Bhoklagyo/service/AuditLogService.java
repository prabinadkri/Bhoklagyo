package com.example.Bhoklagyo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Structured audit logging service for security-critical operations.
 * All audit events are written to a dedicated "audit" logger with
 * structured key=value pairs for easy parsing by log aggregation systems
 * (e.g. Loki, Splunk, ELK).
 */
@Service
public class AuditLogService {

    private static final Logger auditLog = LoggerFactory.getLogger("audit");

    public enum AuditAction {
        USER_REGISTERED,
        USER_LOGIN_SUCCESS,
        USER_LOGIN_FAILED,
        ADMIN_REGISTERED,
        ADMIN_LOGIN_SUCCESS,
        ADMIN_LOGIN_FAILED,
        ROLE_CHANGED,
        USER_DELETED,
        RESTAURANT_DELETED,
        OWNER_ASSIGNED,
        ORDER_CREATED,
        ORDER_STATUS_UPDATED,
        ORDER_FEEDBACK_SUBMITTED,
        EMPLOYEE_INVITED
    }

    /**
     * Log an audit event with the currently authenticated principal.
     *
     * @param action   the audit action type
     * @param details  additional key-value pairs describing the event
     */
    public void log(AuditAction action, Map<String, Object> details) {
        String principal = resolvePrincipal();
        String detailStr = formatDetails(details);

        auditLog.info("action={} principal={} {}", action, principal, detailStr);
    }

    /**
     * Log an audit event with an explicit principal (e.g. before authentication is established).
     */
    public void log(AuditAction action, String principal, Map<String, Object> details) {
        String detailStr = formatDetails(details);
        auditLog.info("action={} principal={} {}", action, principal, detailStr);
    }

    private String resolvePrincipal() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                return auth.getName();
            }
        } catch (Exception ignored) {
            // SecurityContext may not be available
        }
        return "anonymous";
    }

    private String formatDetails(Map<String, Object> details) {
        if (details == null || details.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        details.forEach((key, value) -> {
            if (sb.length() > 0) sb.append(' ');
            sb.append(key).append('=').append(value);
        });
        return sb.toString();
    }
}
