package com.example.haus.service;

import com.example.haus.domain.entity.product.Order;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

public interface VNPayService {


    // sau khi nhấn btn "Thanh Toán": -> vnpay trả về status Success : Fail vào PaymentCallbackComponent (payment/payment-callback) return-url(config)

    public String createVNPayUrl(Long orderId, HttpServletRequest request);

    public boolean checkVNPayCallback(Map<String, String> params);

}
