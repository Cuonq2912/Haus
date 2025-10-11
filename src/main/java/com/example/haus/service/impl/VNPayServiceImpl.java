package com.example.haus.service.impl;

import com.example.haus.config.VNPayConfig;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.entity.product.Order;
import com.example.haus.domain.entity.product.payment.Payment;
import com.example.haus.domain.entity.product.payment.PaymentGateway;
import com.example.haus.domain.entity.product.payment.PaymentStatus;
import com.example.haus.domain.entity.product.payment.PaymentType;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.OrderRepository;
import com.example.haus.repository.PaymentRepository;
import com.example.haus.service.VNPayService;
import com.example.haus.util.PaymentUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    final PaymentRepository paymentRepository;

    @Value("${payment.vnPay.maxTime}")
    int maxPaymentTime;

    @Value("${spring.config.activate.on-profile}")
    static String activeProfile;

    private final String SUCCESS_CODE = "00";

    @Override
    public String createVNPayUrl(Long orderId, HttpServletRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Order.ERR_ORDER_NOT_EXISTED));

        Payment payment = getOrCreatePayment(order);

        Map<String, String> params = vnPayConfig.getConfig();

        Date currentTime = new Date();
        Date expireTime = payment.getExpireAt();

        buildTimeParams(params, expireTime, currentTime);
        
        long amountInVND = Math.round(payment.getAmount() * 100);
        params.put("vnp_Amount", String.valueOf(amountInVND));

        // Tạo mã giao dịch
        String ref = order.getId() + "-" + System.currentTimeMillis();
        params.put("vnp_TxnRef", ref);
        params.put("vnp_OrderInfo", "Payment for order " + order.getId());

        // Lấy IP
        String ipAddr = PaymentUtil.getIpAddress(request, activeProfile);
        params.put("vnp_IpAddr", ipAddr);

        // Tạo hashData
        String hashData = PaymentUtil.createPaymentUrl(params);
        String vnpSecureHash = PaymentUtil.hmacSHA512(vnPayConfig.getVnp_HashSecret(), hashData);

        return vnPayConfig.getVnp_PayUrl() + "?" + hashData + "&vnp_SecureHash=" + vnpSecureHash;
    }

    @NotNull
    private Payment getOrCreatePayment(Order order) {
        Payment payment = order.getPayment();

        if (payment == null) {
            return createPaymentRecord(order);
        }

        if (payment.getType() != PaymentType.ONLINE_PAYMENT) {
            throw new InvalidDataException(ErrorMessage.Order.ERR_PAYMENT_TYPE_INVALID);
        }
        
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new InvalidDataException(ErrorMessage.Order.ERR_PAYMENT_COMPLETED);
        }

        // Payment EXPIRED hoặc CANCELLED
        if (payment.getStatus() == PaymentStatus.EXPIRED || payment.getStatus() == PaymentStatus.CANCELLED) {
            Calendar newExpireTime = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            newExpireTime.add(Calendar.SECOND, maxPaymentTime);
            
            payment.setStatus(PaymentStatus.PENDING);
            payment.setExpireAt(newExpireTime.getTime());
            return paymentRepository.save(payment);
        }

        // Payment PENDING nhưng đã quá hạn
        Date currentTime = new Date();
        if (payment.getStatus() == PaymentStatus.PENDING && currentTime.after(payment.getExpireAt())) {
            Calendar newExpireTime = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            newExpireTime.add(Calendar.SECOND, maxPaymentTime);
            
            payment.setExpireAt(newExpireTime.getTime());
            return paymentRepository.save(payment);
        }
        
        return payment;
    }

    private Payment createPaymentRecord(Order order) {
        Calendar expireTime = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        expireTime.add(Calendar.SECOND, maxPaymentTime);

        Payment payment = Payment.builder()
                .amount(order.getTotalAmount())
                .gateway(PaymentGateway.VNPAY)
                .type(PaymentType.ONLINE_PAYMENT)
                .status(PaymentStatus.PENDING)
                .expireAt(expireTime.getTime())
                .order(order)
                .build();
        
        return paymentRepository.save(payment);
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
    @Transactional
    public Map<String, String> processVNPayIPN(Map<String, String> params) {
        Map<String, String> response = new HashMap<>();
        try{
            if (verifySignature(params)) {
                log.error("IPN: Invalid signature!");
                response.put("RspCode", "97");
                response.put("Message", "Invalid signature");
                return response;
            }

            String txnRef = params.get("vnp_TxnRef");
            String responseCode = params.get("vnp_ResponseCode");
            Long amount = Long.parseLong(params.get("vnp_Amount")) / 100;

            String[] p = txnRef.split("-");
            Long orderId = Long.valueOf(p[0]);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Order.ERR_ORDER_NOT_EXISTED));

            Payment payment = order.getPayment();

            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                log.warn("IPN: Order {} already processed as COMPLETED", orderId);
                response.put("RspCode", "02");
                response.put("Message", "Order already confirmed");
                return response;
            }

            // Verify amount
            if (!payment.getAmount().equals(amount)) {
                log.error("IPN: Amount mismatch! Expected: {}, Got: {}", payment.getAmount(), amount);
                response.put("RspCode", "04");
                response.put("Message", "Invalid amount");
                return response;
            }

            if (SUCCESS_CODE.equals(responseCode)) {
                payment.setStatus(PaymentStatus.COMPLETED);
                order.setStatus(OrderStatus.COMPLETED);
            } else {
                payment.setStatus(PaymentStatus.CANCELLED);
                order.setStatus(OrderStatus.CANCELLED);
            }

            paymentRepository.save(payment);
            orderRepository.save(order);

            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");

        } catch (Exception e){
            response.put("RspCode", "99");
            response.put("Message", "Unknown error");
        }

        return response;
    }

    @Override
    public Map<String, Object> handleVNPayReturn(Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();

        if (verifySignature(params)) {
            log.error("Return: Invalid signature!");
            result.put("success", false);
            result.put("message", "Invalid signature");
            return result;
        }
        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");
        String transactionNo = params.get("vnp_TransactionNo");
        String bankCode = params.get("vnp_BankCode");

        boolean isSuccess = SUCCESS_CODE.equals(responseCode);
        result.put("success", isSuccess);
        result.put("orderId", txnRef.split("-")[0]);
        result.put("transactionNo", transactionNo);
        result.put("bankCode", bankCode);
        result.put("message", isSuccess ? "Thanh toán thành công" : "Thanh toán thất bại");

        log.info("Return: Transaction {}, Success: {}", transactionNo, isSuccess);

        return result;
    }

    private boolean verifySignature(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");

        Map<String, String> fieldsToHash = new HashMap<>(params);
        fieldsToHash.remove("vnp_SecureHash");
        fieldsToHash.remove("vnp_SecureHashType");

        String calculatedHash = PaymentUtil.hashAllFields(fieldsToHash, vnPayConfig.getVnp_HashSecret());

        return !calculatedHash.equalsIgnoreCase(receivedHash);
    }
}
