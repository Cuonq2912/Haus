package com.example.haus.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class PaymentUtil {

    public static String createPaymentUrl(Map<String, String> params) {
        return params.entrySet().stream().filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII) + "="
                        + URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))
                .collect(Collectors.joining("&"));
    }

    public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    public static String hmacSHA256(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac256 = Mac.getInstance("HmacSHA256");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA256");
            hmac256.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac256.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    public static String getIpAddress(HttpServletRequest request, String activeProfile) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress != null && !ipAddress.isEmpty()) {
                // Lấy IP đầu tiên nếu có nhiều
                ipAddress = ipAddress.split(",")[0].trim();
            } else {
                ipAddress = request.getRemoteAddr();
            }

            // Nếu chạy dev và gặp IPv6 localhost thì convert sang IPv4
            if ("dev".equalsIgnoreCase(activeProfile)
                    && ("0:0:0:0:0:0:0:1".equals(ipAddress) || "::1".equals(ipAddress))) {
                ipAddress = "127.0.0.1";
            }
        } catch (Exception e) {
            ipAddress = "Invalid IP: " + e.getMessage();
        }
        return ipAddress;
    }

    public static String hashAllFields(Map<String, String> fields, String hashSecret) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append("=")
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashData.append("&");
                }
            }
        }

        return PaymentUtil.hmacSHA512(hashSecret, hashData.toString());
    }

    public static String createOrderRawSignature(String accessKey, Long amount, String extraData, String ipnUrl,
            String orderId, String orderInfo, String partnerCode, String redirectUrl, String requestId,
            String requestType) {
        return String.format(
                "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                accessKey, amount, extraData, ipnUrl, orderId, orderInfo, partnerCode, redirectUrl, requestId,
                requestType);
    }

    public static String createIpnRawSignature(String accessKey, Long amount, String extraData, String message,
            String orderId, String orderInfo, String orderType, String partnerCode, String payType, String requestId,
            Long responseTime, String resultCode, String transId) {
        return String.format(
                "accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                accessKey, amount, extraData, message, orderId, orderInfo, orderType, partnerCode, payType, requestId,
                responseTime, resultCode, transId);
    }

    public static String encodeExtraData(ObjectMapper objectMapper, Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        try {
            String json = objectMapper.writeValueAsString(data);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> decodeExtraData(ObjectMapper objectMapper, String extraData) {
        if (extraData == null || extraData.isEmpty()) {
            return new HashMap<>();
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(extraData);
            String json = new String(decoded, StandardCharsets.UTF_8);
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

}