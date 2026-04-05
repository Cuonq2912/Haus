package com.example.haus.config.ratelimit;

import com.example.haus.config.ratelimit.impl.InMemoryBucketStorage;
import com.example.haus.config.ratelimit.impl.RedisBucketStorage;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.jedis.cas.JedisBasedProxyManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@Slf4j(topic = "RATE-LIMIT-CONFIG")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RateLimitConfig {
    @Value("${spring.redis.host}")
    String redisHost;

    @Value("${spring.redis.port}")
    int redisPort;

    @Value("${spring.redis.password}")
    String redisPassword;

    // dev - environment
    @Bean
    @ConditionalOnProperty(name = "rate-limit.storage-type", havingValue = "memory", matchIfMissing = true)
    public BucketStorage inMemoryBucketStorage() {
        log.info("Initializing in-memory rate limit storage");
        return new InMemoryBucketStorage();
    }

    @Bean
    @ConditionalOnProperty(name = "rate-limit.storage-type", havingValue = "redis")
    public BucketStorage redisBucketStorage() {
        log.info("Initializing Redis-based rate limiting storage");

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(64);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setBlockWhenExhausted(true);

        JedisPool jedisPool;
        if (redisPassword != null && !redisPassword.isEmpty()) {
            jedisPool = new JedisPool(poolConfig, redisHost, redisPort, 2000, redisPassword);
        } else {
            jedisPool = new JedisPool(poolConfig, redisHost, redisPort);
        }

        ProxyManager<byte[]> proxyManager = JedisBasedProxyManager.builderFor(jedisPool).withExpirationStrategy(
                ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(1))).build();

        return new RedisBucketStorage(proxyManager);
    }

}
