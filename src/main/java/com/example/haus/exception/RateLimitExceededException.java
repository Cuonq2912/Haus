package com.example.haus.exception;

import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {

    private final long retryAfterSeconds;
    private final String endpoint;

    public RateLimitExceededException(String message, long retryAfterSeconds, String endpoint) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
        this.endpoint = endpoint;
    }

    public RateLimitExceededException(long retryAfterSeconds, String endpoint) {
        super(String.format("Rate limit exceeded for endpoint: %s. Please retry after %d seconds.", endpoint,
                retryAfterSeconds));
        this.retryAfterSeconds = retryAfterSeconds;
        this.endpoint = endpoint;
    }
}
