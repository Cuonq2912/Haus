package com.example.haus.config.ratelimit.impl;

import com.example.haus.config.ratelimit.BucketStorage;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Duration;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class RedisBucketStorage implements BucketStorage {

    ProxyManager<byte[]> proxyManager;

    @Override
    public Bucket resolveBucket(String key, long capacity, long refillTokens, Duration refillDuration) {
        BucketConfiguration config = BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(capacity, Refill.intervally(refillTokens, refillDuration))).build();
        return proxyManager.builder().build(key.getBytes(), () -> config);
    }

    @Override
    public void removeBucket(String key) {
        proxyManager.removeProxy(key.getBytes());
    }

}
