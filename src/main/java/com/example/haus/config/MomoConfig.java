package com.example.haus.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoConfig {

    @Value("${payment.momo.partner-code}")
    String partnerCode;

    @Value("${payment.momo.access-key}")
    String accessKey;

    @Value("${payment.momo.secret-key}")
    String secretKey;

    @Value("${payment.momo.end-point}")
    String endPoint;

    @Value("${payment.momo.redirect-url}")
    String redirectUrl;

    @Value("${payment.momo.ipn-url}")
    String ipnUrl;

    @Value("${payment.momo.request-type}")
    String requestType;


    public Map<String, Object> buildCreateOrderParams(String requestId, String orderId, 
                                                       Long amount, String orderInfo, 
                                                       String extraData) {
        Map<String, Object> params = new HashMap<>();
        params.put("partnerCode", this.partnerCode);
        params.put("storeId", "HausStore");
        params.put("requestId", requestId);
        params.put("amount", amount);
        params.put("orderId", orderId);
        params.put("orderInfo", orderInfo);
        params.put("redirectUrl", this.redirectUrl);
        params.put("ipnUrl", this.ipnUrl);
        params.put("lang", "vi");
        params.put("extraData", extraData);
        params.put("requestType", this.requestType);
        return params;
    }

}
