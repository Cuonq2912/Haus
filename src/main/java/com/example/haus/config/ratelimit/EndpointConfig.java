package com.example.haus.config.ratelimit;

import com.example.haus.constant.CommonConstant;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Duration;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointConfig {

    String path;
    Long capacity;
    Long refillTokens;
    Duration refillDuration;
    boolean byIp = CommonConstant.FALSE;
    boolean disabled = CommonConstant.FALSE;
}
