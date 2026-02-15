package com.example.Bhoklagyo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis-backed sliding-window rate limiter.
 * Limits requests per client IP within a configurable time window.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);
    private static final String RATE_LIMIT_PREFIX = "rate_limit:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rate.limit.enabled:true}")
    private boolean enabled;

    @Value("${rate.limit.requests:100}")
    private int maxRequests;

    @Value("${rate.limit.window-seconds:60}")
    private int windowSeconds;

    public RateLimitingFilter(RedisTemplate<String, Object> redisTemplate,
                              ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        // Skip rate limiting for actuator and health endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        String key = RATE_LIMIT_PREFIX + clientIp;

        try {
            Long currentCount = redisTemplate.opsForValue().increment(key);
            if (currentCount != null && currentCount == 1) {
                redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
            }

            // Set rate limit headers
            long remaining = Math.max(0, maxRequests - (currentCount != null ? currentCount : 0));
            response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
            response.setHeader("X-RateLimit-Window", windowSeconds + "s");

            if (currentCount != null && currentCount > maxRequests) {
                log.warn("Rate limit exceeded for IP: {} ({}/{} in {}s)", clientIp, currentCount, maxRequests, windowSeconds);

                Long ttl = redisTemplate.getExpire(key);
                response.setHeader("Retry-After", String.valueOf(ttl != null ? ttl : windowSeconds));

                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                Map<String, Object> body = new HashMap<>();
                body.put("timestamp", LocalDateTime.now().toString());
                body.put("status", 429);
                body.put("error", "Too Many Requests");
                body.put("message", "Rate limit exceeded. Try again in " + (ttl != null ? ttl : windowSeconds) + " seconds.");
                body.put("path", path);

                objectMapper.writeValue(response.getOutputStream(), body);
                return;
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // If Redis is down, allow the request through (fail-open)
            log.warn("Rate limiting unavailable (Redis error): {}", e.getMessage());
            filterChain.doFilter(request, response);
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
