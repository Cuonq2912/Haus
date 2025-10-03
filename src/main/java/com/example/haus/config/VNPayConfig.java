package com.example.haus.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Getter
public class VNPayConfig {

    @Value("${payment.vnPay.url}")
    private String vnp_PayUrl;

    @Value("${payment.vnPay.returnUrl}")
    private String vnp_ReturnUrl;

    @Value("${payment.vnPay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${payment.vnPay.hashSecret}")
    private String vnp_HashSecret;

    @Value("${payment.vnPay.version}")
    private String vnp_Version;

    @Value("${payment.vnPay.command:pay}")
    private String vnp_Command;

    @Value("${payment.vnPay.orderType}")
    private String vnp_OrderType;

    @Value("${payment.vnPay.currCode:VND}")
    private String vnp_CurrCode;

    @Value("${payment.vnPay.locale:vn}")
    private String vnp_Locale;



    public Map<String, String> getConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_Version", this.vnp_Version);
        vnpParamsMap.put("vnp_Command", this.vnp_Command);
        vnpParamsMap.put("vnp_TmnCode", this.vnp_TmnCode);
        vnpParamsMap.put("vnp_CurrCode", this.vnp_CurrCode);
        vnpParamsMap.put("vnp_Locale", this.vnp_Locale);
        vnpParamsMap.put("vnp_ReturnUrl", this.vnp_ReturnUrl);
        vnpParamsMap.put("vnp_OrderType", this.vnp_OrderType);
        return vnpParamsMap;
    }

}