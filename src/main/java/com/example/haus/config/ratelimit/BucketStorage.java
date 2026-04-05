package com.example.haus.config.ratelimit;

import io.github.bucket4j.Bucket;

import java.time.Duration;

public interface BucketStorage {
    Bucket resolveBucket(String key, long capacity, long refillTokens, Duration refillDuration);

    void removeBucket(String key);

}
