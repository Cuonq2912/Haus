package com.example.haus.service.impl;

import com.example.haus.config.VNPayConfig;
import com.example.haus.constant.CommonConstant;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.entity.product.Order;
import com.example.haus.domain.entity.product.payment.Payment;
import com.example.haus.domain.entity.product.payment.PaymentGateway;
import com.example.haus.domain.entity.product.payment.PaymentStatus;
import com.example.haus.domain.entity.product.payment.PaymentType;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.OrderRepository;
import com.example.haus.service.VNPayService;
import com.example.haus.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j(topic = "VNPAY-SERVICE")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VNPayServiceImpl implements VNPayService {

    final OrderRepository orderRepository;

    final VNPayConfig vnPayConfig;

    VNPayService vNPayService;

    @Value("${payment.vnPay.maxTime}")
    int maxPaymentTime;

    @Value("${spring.profiles.active}")
    static String activeProfile;

    private final String SUCCESS_CODE = "00";


    @Override
    public String createVNPayUrl(Long orderId, HttpServletRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Order.ERR_ORDER_NOT_EXISTED));

        Payment payment = getPayment(order);

        Date currentTime = new Date();
        Date expireTime = payment.getExpireAt();

        if (currentTime.after(expireTime)) {
            payment.setStatus(PaymentStatus.EXPIRED);
            order.setPayment(payment);
            orderRepository.save(order);
            throw new ResourceNotFoundException(ErrorMessage.Order.ERR_PAYMENT_EXPIRED);
        }

        Map<String, String> params = vnPayConfig.getConfig();
        buildTimeParams(params, expireTime, currentTime);
        params.put("vnp_Amount", String.valueOf(payment.getAmount() * 100L));

        // Tạo mã giao dịch
        String ref = order.getId() + "-" + System.currentTimeMillis();
        params.put("vnp_TxnRef", ref);
        params.put("vnp_OrderInfo", "Thanh toán đơn hàng " + order.getId());

        // Lấy IP
        String ipAddr = VNPayUtil.getIpAddress(request, activeProfile);
        params.put("vnp_IpAddr", ipAddr);

        String query = VNPayUtil.createPaymentUrl(params, true);
        String hashData = VNPayUtil.createPaymentUrl(params, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getVnp_HashSecret(), hashData);

        query += "&vnp_SecureHash=" + vnpSecureHash;
        return vnPayConfig.getVnp_PayUrl() + "?" + query;
    }


    @NotNull
    private static Payment getPayment(Order order) {
        Payment payment = order.getPayment();
        if (payment == null) {
            throw new ResourceNotFoundException(ErrorMessage.Order.ERR_PAYMENT_NOT_FOUND);
        }
        if (payment.getType() != PaymentType.BANK_TRANSFER) {
            throw new ResourceNotFoundException(ErrorMessage.Order.ERR_PAYMENT_TYPE_INVALID);
        }
        if (payment.getStatus() == PaymentStatus.EXPIRED) {
            throw new ResourceNotFoundException(ErrorMessage.Order.ERR_PAYMENT_EXPIRED);
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new ResourceNotFoundException(ErrorMessage.Order.ERR_PAYMENT_STATUS_INVALID);
        }
        return payment;
    }


    private void buildTimeParams(Map<String, String> params, Date expiredTime, Date currentTime) {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        // Thời gian còn lại (giây)
        long diffInMillis = expiredTime.getTime() - currentTime.getTime();
        int remainingSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(diffInMillis);

        // Thời gian cho phép tối đa
        int allowedTime = Math.min(remainingSeconds, maxPaymentTime);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        // Ngày tạo giao dịch
        String vnp_CreateDate = formatter.format(now.getTime());
        params.put("vnp_CreateDate", vnp_CreateDate);

        // Ngày hết hạn giao dịch (tính từ now + allowedTime)
        now.add(Calendar.SECOND, allowedTime);
        String vnp_ExpireDate = formatter.format(now.getTime());
        params.put("vnp_ExpireDate", vnp_ExpireDate);
    }

    @Override
    public boolean checkVNPayCallback(Map<String, String> params) {

        String vnp_SecureHash = params.get("vnp_SecureHash");

        Map<String, String> fieldsToHash = new HashMap<>(params);
        fieldsToHash.remove("vnp_SecureHash");
        fieldsToHash.remove("vnp_SecureHashType");

        String signValue = VNPayUtil.hashAllFields(params);
        if (!signValue.equals(vnp_SecureHash)) {
            log.error("Invalid signature! Expected: {}, Got: {}", signValue, vnp_SecureHash);
            return false;
        }

        String txnRef = params.get("vnp_TxnRef");
        String[] p = txnRef.split("-");
        Order order = orderRepository.findById(Long.valueOf(p[0]))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Order.ERR_ORDER_NOT_EXISTED));

        boolean checkPayment = checkPaymentResponse(order, params);
        if(checkPayment){
            Payment payment = order.getPayment();
            payment.setStatus(PaymentStatus.COMPLETED);
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);
            return true;
        }
        else return false;
    }

    private boolean checkPaymentResponse(Order order, Map<String, String> params) {
        String code = params.get("vnp_ResponseCode");
        if(code.equals(SUCCESS_CODE)){
            long amount = Long.parseLong(params.get("vnp_Amount")) / 100L;
            Payment payment = order.getPayment();
            return amount == payment.getAmount();
        }
        else return false;
    }
}
