package com.example.haus.config.ratelimit.impl;

import com.example.haus.config.ratelimit.BucketStorage;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InMemoryBucketStorage implements BucketStorage {

    ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public Bucket resolveBucket(String key, long capacity, long refillTokens, Duration refillDuration) {
        return buckets.computeIfAbsent(key, k -> createBucket(capacity, refillTokens, refillDuration)
        );
    }

    private Bucket createBucket(long capacity, long refillTokens, Duration refillDuration) {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.intervally(refillTokens, refillDuration));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    public void removeBucket(String key) {
        buckets.remove(key);
    }
}