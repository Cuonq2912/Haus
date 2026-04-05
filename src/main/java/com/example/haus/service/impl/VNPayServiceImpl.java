package com.example.haus.service.impl;

import com.example.haus.config.VNPayConfig;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.entity.product.Order;
import com.example.haus.domain.entity.product.OrderItem;
import com.example.haus.domain.entity.product.ProductVariation;
import com.example.haus.domain.entity.product.payment.Payment;
import com.example.haus.domain.entity.product.payment.PaymentGateway;
import com.example.haus.domain.entity.product.payment.PaymentStatus;
import com.example.haus.domain.entity.product.payment.PaymentType;
import com.example.haus.domain.entity.user.Role;
import com.example.haus.domain.entity.user.User;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.OrderRepository;
import com.example.haus.repository.PaymentRepository;
import com.example.haus.repository.ProductVariationRepository;
import com.example.haus.repository.UserRepository;
import com.example.haus.service.VNPayService;
import com.example.haus.util.PaymentUtil;
import com.example.haus.util.UpdateSoldQuantityUtil;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.example.haus.constant.CommonConstant.*;

@Service
@Slf4j(topic = "VNPAY-SERVICE")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VNPayServiceImpl implements VNPayService {

    final OrderRepository orderRepository;

    final VNPayConfig vnPayConfig;

    final PaymentRepository paymentRepository;

    final ProductVariationRepository productVariationRepository;

    final UpdateSoldQuantityUtil updateSoldQuantityUtil;

    final UserRepository userRepository;

    Map<String, Boolean> checkIpnList = new ConcurrentHashMap<>();

    @Value("${payment.vnPay.maxTime}")
    int maxPaymentTime;

    @Value("${spring.config.activate.on-profile}")
    String activeProfile;

    private static final String SUCCESS_CODE = "00";

    @Override
    @Transactional
    public String createVNPayUrl(Long orderId, String username, HttpServletRequest request) {
        Order order = getAccessibleOrder(orderId, username);

        if (!order.getPayment().getGateway().equals(PaymentGateway.VNPAY) || !order.getPayment().getType().equals(PaymentType.ONLINE_PAYMENT)) {
            throw new InvalidDataException("Payment type invalid");
        }

        Payment payment = getOrCreatePayment(order);

        Map<String, String> params = vnPayConfig.getConfig();

        Date currentTime = new Date();
        Date expireTime = payment.getExpireAt();

        buildTimeParams(params, expireTime, currentTime);
        
        long amountInVND = Math.round(payment.getAmount() * 100);
        params.put("vnp_Amount", String.valueOf(amountInVND));

        // Tạo mã giao dịch
        String ref = order.getId() + "-" + System.currentTimeMillis();
        params.put(VNP_TXN_REF, ref);
        params.put("vnp_OrderInfo", "Payment for order " + order.getId());

        // Lấy IP
        String ipAddr = PaymentUtil.getIpAddress(request, activeProfile);
        params.put("vnp_IpAddr", ipAddr);

        // Tạo hashData
        String hashData = PaymentUtil.createPaymentUrl(params);
        String vnpSecureHash = PaymentUtil.hmacSHA512(vnPayConfig.getVnPayHashSecret(), hashData);

        return vnPayConfig.getVnPayUrl() + "?" + hashData + "&vnp_SecureHash=" + vnpSecureHash;
    }

    private Order getAccessibleOrder(Long orderId, String username) {
        User currentUser = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Order.ERR_ORDER_NOT_EXISTED));

        if (currentUser.getRole() != Role.ADMIN && !order.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException(ErrorMessage.Order.ERR_ORDER_NOT_EXISTED);
        }

        return order;
    }

    @NotNull
    @Transactional
    protected Payment getOrCreatePayment(Order order) {
        Payment payment = order.getPayment();

        if (payment == null) {
            return createPaymentRecord(order);
        }

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new InvalidDataException(ErrorMessage.Order.ERR_PAYMENT_COMPLETED);
        }

        if (payment.getExpireAt() == null) {
            Calendar newExpireTime = Calendar.getInstance(TimeZone.getTimeZone(ETC_GMT7));
            newExpireTime.add(Calendar.SECOND, maxPaymentTime);
            payment.setExpireAt(newExpireTime.getTime());
        }

        if (payment.getStatus() == PaymentStatus.PENDING) {
            payment.setType(PaymentType.ONLINE_PAYMENT);
            payment.setGateway(PaymentGateway.VNPAY);
            paymentRepository.save(payment);
        }

        // Payment EXPIRED hoặc CANCELLED
        if (payment.getStatus() == PaymentStatus.EXPIRED || payment.getStatus() == PaymentStatus.CANCELLED) {
            Calendar newExpireTime = Calendar.getInstance(TimeZone.getTimeZone(ETC_GMT7));
            newExpireTime.add(Calendar.SECOND, maxPaymentTime);
            
            payment.setStatus(PaymentStatus.PENDING);
            payment.setExpireAt(newExpireTime.getTime());
            return paymentRepository.save(payment);
        }

        // Payment PENDING nhưng đã quá hạn
        Date currentTime = new Date();
        if (payment.getStatus() == PaymentStatus.PENDING && !currentTime.before(payment.getExpireAt())) {
            Calendar newExpireTime = Calendar.getInstance(TimeZone.getTimeZone(ETC_GMT7));
            newExpireTime.add(Calendar.SECOND, maxPaymentTime);
            
            payment.setExpireAt(newExpireTime.getTime());
            return paymentRepository.save(payment);
        }
        
        return payment;
    }

    private Payment createPaymentRecord(Order order) {
        Calendar expireTime = Calendar.getInstance(TimeZone.getTimeZone(ETC_GMT7));
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
        // Validate input parameters
        if (expiredTime == null || currentTime == null) {
            throw new InvalidDataException("Expired time and current time must not be null");
        }
        
        TimeZone vnTimeZone = TimeZone.getTimeZone(ETC_GMT7);
        Calendar now = Calendar.getInstance(vnTimeZone);

        // Thời gian còn lại (giây)
        long diffInMillis = expiredTime.getTime() - currentTime.getTime();
        int remainingSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(diffInMillis);

        // Thời gian cho phép tối đa
        int allowedTime = Math.max(0, Math.min(remainingSeconds, maxPaymentTime));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(vnTimeZone);

        // Ngày tạo giao dịch
        String vnpCreateDate = formatter.format(now.getTime());
        params.put("vnp_CreateDate", vnpCreateDate);

        // Ngày hết hạn giao dịch (tính từ now + allowedTime)
        now.add(Calendar.SECOND, allowedTime);
        String vnpExpireDate = formatter.format(now.getTime());
        params.put("vnp_ExpireDate", vnpExpireDate);
    }

    @Override
    @Transactional
    public Map<String, String> processVNPayIPN(Map<String, String> params) {
        Map<String, String> response = new HashMap<>();

            log.info("IPN: Received params: {}", params);
            
            if (!verifySignature(params)) {  // ✅ ĐÚNG: Nếu signature KHÔNG hợp lệ
                log.error("IPN: Invalid signature!");
                response.put(RSP_CODE, "97");
                response.put(MESSAGE, "Invalid signature");
                return response;
            }
            
            log.info("IPN: Signature verified successfully");

            String txnRef = params.get(VNP_TXN_REF);
            String responseCode = params.get("vnp_ResponseCode");
            Long amount = Long.parseLong(params.get("vnp_Amount")) / 100;

            String[] p = txnRef.split("-");
            Long orderId = Long.valueOf(p[0]);
            
            log.info("IPN: Processing order {} with responseCode: {}", orderId, responseCode);
            
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Order.ERR_ORDER_NOT_EXISTED));

            Payment payment = order.getPayment();

            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                log.warn("IPN: Order {} already processed as COMPLETED", orderId);
                response.put(RSP_CODE, "02");
                response.put(MESSAGE, "Order already confirmed");
                return response;
            }

            long expectedAmount = Math.round(payment.getAmount());
            if (expectedAmount != amount) {
                log.error("IPN: Amount mismatch! Expected: {}, Got: {}", expectedAmount, amount);
                response.put(RSP_CODE, "04");
                response.put(MESSAGE, "Invalid amount");
                return response;
            }

            if (SUCCESS_CODE.equals(responseCode)) {
                updateInventoryForCompletedOrder(order);
                payment.setStatus(PaymentStatus.COMPLETED);
                order.setStatus(OrderStatus.COMPLETED);
                log.info("IPN: Order {} marked as COMPLETED", orderId);
            } else {
                payment.setStatus(PaymentStatus.CANCELLED);
                order.setStatus(OrderStatus.CANCELLED);
                log.info("IPN: Order {} marked as CANCELLED", orderId);
            }

            paymentRepository.save(payment);
            orderRepository.save(order);
            
            log.info("IPN: Database updated successfully for order{}", orderId);

            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");
            checkIpnList.put(params.get(VNP_TXN_REF).split("-")[0], true);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> handleVNPayReturn(Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();

        if (!verifySignature(params)) {  // ĐÚNG: Nếu signature KHÔNG hợp lệ
            log.error("Return: Invalid signature!");
            result.put("success", false);
            result.put("message", "Invalid signature");
            return result;
        }
        String responseCode = params.get("vnp_ResponseCode");

        boolean isSuccess = SUCCESS_CODE.equals(responseCode)
                && Boolean.TRUE.equals(checkIpnList.get(params.get("vnp_TxnRef").split("-")[0]));

        result.put("message", isSuccess ? "Thanh toán thành công" : "Thanh toán thất bại");

        checkIpnList.remove(params.get("vnp_TxnRef").split("-")[0]);

        log.info("Response code = {} and success = {}", responseCode, isSuccess);

        return result;
    }

    private boolean verifySignature(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");

        Map<String, String> fieldsToHash = new HashMap<>(params);
        fieldsToHash.remove("vnp_SecureHash");
        fieldsToHash.remove("vnp_SecureHashType");

        String calculatedHash = PaymentUtil.hashAllFields(fieldsToHash, vnPayConfig.getVnPayHashSecret());

        return calculatedHash.equalsIgnoreCase(receivedHash);  // ✅ ĐÚNG: TRUE = valid, FALSE = invalid
    }

    public void updateInventoryForCompletedOrder(Order order) {
        if (order == null) {
            log.warn("Attempted to update inventory for a null or empty order.");
            throw new InvalidDataException(ErrorMessage.Order.ERR_ORDER_ITEMS_EMPTY);
        }

        Set<Long> productIdsToUpdate = new HashSet<>();
        Double totalAmountCheck = 0.0;

        for (OrderItem item : order.getOrderItems()) {
            ProductVariation variation = item.getProductVariation();
            int purchasedQuantity = item.getQuantity();

            if (variation == null) {
                log.error("Order Item {} is missing Product Variation.", item.getId());
                continue;
            }

            int currentInventory = variation.getInventoryQuantity();

            if (currentInventory < purchasedQuantity) {
                throw new InvalidDataException("Insufficient inventory for product variation " + variation.getId());
            }

            variation.setInventoryQuantity(currentInventory - purchasedQuantity);
            variation.setSoldQuantity(variation.getSoldQuantity() + purchasedQuantity);

            variation.setUpdatedAt(new Date());
            totalAmountCheck += item.getPriceAtSale() * item.getQuantity();

            productVariationRepository.save(variation);

            productIdsToUpdate.add(variation.getProduct().getId());
        }
        totalAmountCheck += order.getShippingFee();

        log.info("totalCheck = {}", totalAmountCheck);
        log.info("totaorder.getTotalAmount()lCheck = {}", order.getTotalAmount());

        if (!(totalAmountCheck.toString().equals(order.getTotalAmount().toString()))) {
            throw new InvalidDataException(ErrorMessage.Payment.ERR_ORDER_TOTAL_AMOUNT_NOT_MATCH);
        }

        for (Long productId : productIdsToUpdate) {
            updateSoldQuantityUtil.updateProductTotalInventoryAndSoldQuantity(productId);
        }
    }
}
