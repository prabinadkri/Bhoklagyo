package com.example.Bhoklagyo.exception;

/**
 * Thrown when a client exceeds the configured rate limit.
 */
public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}
