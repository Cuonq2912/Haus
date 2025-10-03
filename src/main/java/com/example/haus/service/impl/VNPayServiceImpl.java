package com.example.haus.service.impl;

import com.example.haus.config.VNPayConfig;
import com.example.haus.constant.CommonConstant;
import com.example.haus.constant.ErrorMessage;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j(topic = "VNPAY-SERVICE")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VNPayServiceImpl implements VNPayService {

    final OrderRepository orderRepository;

    final VNPayConfig vnPayConfig;

    @Value("${payment.vnPay.maxTime}")
    int maxPaymentTime;

    @Value("${spring.profiles.active}")
    static String activeProfile;

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
    public boolean checkVNPayReturn(Map<String, String> params) {


    }

//    public int orderReturn(HttpServletRequest request){
//        Map fields = new HashMap();
//        for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
//            String fieldName = null;
//            String fieldValue = null;
//            try {
//                fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
//                fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            if ((fieldValue != null) && (fieldValue.length() > 0)) {
//                fields.put(fieldName, fieldValue);
//            }
//        }
//
//        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
//        if (fields.containsKey("vnp_SecureHashType")) {
//            fields.remove("vnp_SecureHashType");
//        }
//        if (fields.containsKey("vnp_SecureHash")) {
//            fields.remove("vnp_SecureHash");
//        }
//        String signValue = VNPayConfig.hashAllFields(fields);
//        if (signValue.equals(vnp_SecureHash)) {
//            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
//                return 1;
//            } else {
//                return 0;
//            }
//        } else {
//            return -1;
//        }
//    }


}
