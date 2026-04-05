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
    private String vnPayUrl;

    @Value("${payment.vnPay.returnUrl}")
    private String vnPayReturnUrl;

    @Value("${payment.vnPay.tmnCode}")
    private String vnPayTmnCode;

    @Value("${payment.vnPay.hashSecret}")
    private String vnPayHashSecret;

    @Value("${payment.vnPay.version}")
    private String vnPayVersion;

    @Value("${payment.vnPay.command:pay}")
    private String vnPayCommand;

    @Value("${payment.vnPay.orderType}")
    private String vnPayOrderType;

    @Value("${payment.vnPay.currCode:VND}")
    private String vnPayCurrCode;

    @Value("${payment.vnPay.locale:vn}")
    private String vnPayLocale;

    public Map<String, String> getConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_Version", this.vnPayVersion);
        vnpParamsMap.put("vnp_Command", this.vnPayCommand);
        vnpParamsMap.put("vnp_TmnCode", this.vnPayTmnCode);
        vnpParamsMap.put("vnp_CurrCode", this.vnPayCurrCode);
        vnpParamsMap.put("vnp_Locale", this.vnPayLocale);
        vnpParamsMap.put("vnp_ReturnUrl", this.vnPayReturnUrl);
        vnpParamsMap.put("vnp_OrderType", this.vnPayOrderType);
        return vnpParamsMap;
    }

}