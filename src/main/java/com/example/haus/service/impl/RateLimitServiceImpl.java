package com.example.haus.service.impl;

import com.example.haus.config.ratelimit.BucketStorage;
import com.example.haus.config.ratelimit.EndpointConfig;
import com.example.haus.config.ratelimit.LimitConfig;
import com.example.haus.config.ratelimit.RateLimitProperties;
import com.example.haus.exception.RateLimitExceededException;
import com.example.haus.service.RateLimitService;
import com.example.haus.util.RateLimitUtil;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "RATE_LIMIT_SERVICE")
public class RateLimitServiceImpl implements RateLimitService {

    RateLimitProperties rateLimitProperties;
    BucketStorage bucketStorage;

    @Override
    public boolean allowRequest(HttpServletRequest request) {
        if(!rateLimitProperties.isEnabled()) {
            return true;
        }

        String clientIp = RateLimitUtil.getClientIp(request);

        // Kiểm tra IP có trong whitelist không
        if(RateLimitUtil.isIpWhitelisted(clientIp, rateLimitProperties.getWhitelistIps())){
            log.debug("IP {} is whitelisted, skipping rate limit", clientIp);
            return true;
        }

        String endpoint =request.getRequestURI();
        Optional<EndpointConfig> endpointConfig = findEndpointConfig(endpoint);

        if (endpointConfig.isPresent() && endpointConfig.get().isDisabled()){
            log.debug("Rate limiting disabled for endpoint: {}", endpoint);
            return true;
        }
        // Xác định identifier
        String identifier = resolveIdentifier(request, endpointConfig);
        String key = RateLimitUtil.buildKey(identifier, endpoint); // bucket key

        long capacity;
        long refillTokens;
        Duration refillDuration;
        //Lấy cấu hình limit (từ endpoint config || default)
        if(endpointConfig.isPresent()){
            EndpointConfig config = endpointConfig.get();
            capacity = config.getCapacity();
            refillTokens = config.getRefillTokens();
            refillDuration = config.getRefillDuration();
        } else {
            LimitConfig defaultLimit = rateLimitProperties.getDefaultLimit();
            capacity = defaultLimit.getCapacity();
            refillTokens = defaultLimit.getRefillTokens();
            refillDuration = defaultLimit.getRefillDuration();
        }

        Bucket bucket = bucketStorage.resolveBucket(key, capacity, refillTokens, refillDuration);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if(probe.isConsumed()){
            log.debug("Request allowed for key: {}, remaining tokens: {}", key, probe.getRemainingTokens());
            return true;
        } else {
            long waitForRefill = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());
            log.warn("Rate limit exceeded for key: {}, endpoint: {}, retry after: {} seconds",
                    key, endpoint, waitForRefill);
            throw new RateLimitExceededException(waitForRefill, endpoint);
        }

    }

    private Optional<EndpointConfig> findEndpointConfig(String endpoint){
        return rateLimitProperties.getEndpoints().stream()
                .filter(config -> matchesPath(endpoint, config.getPath()))
                .findFirst();
    }

    private boolean matchesPath(String endpoint, String pattern){
        if(pattern == null || endpoint == null) return false;

        if(endpoint.equals(pattern) || endpoint.startsWith(pattern)) return true;

        if(pattern.endsWith("/**")){
            String prefix = pattern.substring(0, pattern.length() - 3);
            return endpoint.startsWith(prefix);
        }
        return false;
    }

    private String resolveIdentifier(HttpServletRequest request, Optional<EndpointConfig> endpointConfig) {
        if(endpointConfig.isPresent() && endpointConfig.get().isByIp()){
            return "ip:" + RateLimitUtil.getClientIp(request);
        }

        String userId = RateLimitUtil.getUserId();
        if(userId != null) return "user:" + userId;
        return "ip:" + RateLimitUtil.getClientIp(request);
    }
}
