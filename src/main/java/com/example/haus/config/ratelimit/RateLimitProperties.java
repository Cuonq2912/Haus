package com.example.haus.config.ratelimit;

import com.example.haus.constant.CommonConstant;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "rate-limit")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RateLimitProperties {

    boolean enabled = CommonConstant.TRUE;

    String storageType = CommonConstant.MEMORY;

    List<String> whitelistIps = new ArrayList<>(); // ip được bypass

    LimitConfig defaultLimit = new LimitConfig(1000L, 1000L, Duration.ofHours(1));

    LimitConfig perIpLimit = new LimitConfig(200L, 200L, Duration.ofMinutes(10));

    List<EndpointConfig> endpoints = new ArrayList<>();
}
