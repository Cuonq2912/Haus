package com.example.haus.service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface VNPayService {

    String createVNPayUrl(Long orderId, String username, HttpServletRequest request);

    // Xử lý IPN từ VNPay Server(Update db)
    Map<String, String> processVNPayIPN(Map<String, String> params);

    // Xử lý Return URL từ Browser(verify và hiển thị)
    Map<String, Object> handleVNPayReturn(Map<String, String> params);

}
