package com.example.haus.helper;

import com.example.haus.config.MomoConfig;
import com.example.haus.domain.dto.request.product.momo.MomoIpnRequestDto;
import com.example.haus.util.PaymentUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "MOMO-HELPER")
public class MomoHelper {

    MomoConfig momoConfig;

    public String createSignature(Map<String, Object> params) {
        String rawSignature = PaymentUtil.createOrderRawSignature(
                momoConfig.getAccessKey(),
                (Long) params.get("amount"),
                (String) params.get("extraData"),
                (String) params.get("ipnUrl"),
                (String) params.get("orderId"),
                (String) params.get("orderInfo"),
                (String) params.get("partnerCode"),
                (String) params.get("redirectUrl"),
                (String) params.get("requestId"),
                (String) params.get("requestType"));

        log.debug("MoMo raw signature: {}", rawSignature);
        return PaymentUtil.hmacSHA256(momoConfig.getSecretKey(), rawSignature);
    }

    public boolean verifySignature(MomoIpnRequestDto request) {
        try {
            String rawSignature = PaymentUtil.createIpnRawSignature(
                    momoConfig.getAccessKey(),
                    request.getAmount(),
                    request.getExtraData(),
                    request.getMessage(),
                    request.getOrderId(),
                    request.getOrderInfo(),
                    request.getOrderType(),
                    request.getPartnerCode(),
                    request.getPayType(),
                    request.getRequestId(),
                    request.getResponseTime(),
                    request.getResultCode(),
                    request.getTransId());

            String expectedSignature = PaymentUtil.hmacSHA256(momoConfig.getSecretKey(), rawSignature);
            boolean isValid = expectedSignature.equals(request.getSignature());
            
            if (!isValid) {
                log.warn("Invalid MoMo signature. Expected: {}, Received: {}", 
                        expectedSignature, request.getSignature());
            }
            
            return isValid;

        } catch (Exception e) {
            log.error("Error verifying MoMo signature: ", e);
            return false;
        }
    }
}

