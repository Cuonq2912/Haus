package com.example.haus.service.impl;

import com.example.haus.config.MomoConfig;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.dto.request.product.momo.MomoIpnRequestDto;
import com.example.haus.domain.dto.response.product.momo.MomoCreateOrderResponseDto;
import com.example.haus.domain.entity.product.Order;
import com.example.haus.domain.entity.product.payment.*;
import com.example.haus.domain.entity.user.Role;
import com.example.haus.domain.entity.user.User;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.helper.MomoHelper;
import com.example.haus.repository.MomoTransactionRepository;
import com.example.haus.repository.OrderRepository;
import com.example.haus.repository.PaymentRepository;
import com.example.haus.repository.UserRepository;
import com.example.haus.service.MomoService;
import com.example.haus.util.PaymentUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.example.haus.constant.CommonConstant.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "MOMO-SERVICE")
public class MomoServiceImpl implements MomoService {

    private static final long MIN_AMOUNT = 1_000L;
    private static final long MAX_AMOUNT = 50_000_000L;

    MomoConfig momoConfig;

    MomoHelper momoHelper;

    OrderRepository orderRepository;

    PaymentRepository paymentRepository;

    MomoTransactionRepository momoTransactionRepository;

    UserRepository userRepository;

    RestTemplate restTemplate;

    ObjectMapper objectMapper;

    @Override
    @Transactional
    public Map<String, String> createPaymentOrder(Long orderId, String username) throws JsonProcessingException {

        Order order = getAccessibleOrder(orderId, username);

        if (!order.getPayment().getGateway().equals(PaymentGateway.MOMO)
                || !order.getPayment().getType().equals(PaymentType.ONLINE_PAYMENT)) {
            throw new InvalidDataException("Payment gateway | type invalid");
        }

        long amount = order.getTotalAmount().longValue();

        // validate
        if (amount < MIN_AMOUNT)
            throw new InvalidDataException(ErrorMessage.Payment.MOMO_AMOUNT_TOO_LOW);

        if (amount > MAX_AMOUNT)
            throw new InvalidDataException(ErrorMessage.Payment.MOMO_AMOUNT_TOO_HIGH);

        if (order.getStatus() == OrderStatus.COMPLETED)
            throw new InvalidDataException(ErrorMessage.Payment.MOMO_ORDER_ALREADY_PAID);

        Payment payment = getOrCreatePayment(order);

        // Tạo request params
        String requestId = UUID.randomUUID().toString();
        String orderIdMomo = "ORD_" + orderId + "_" + UUID.randomUUID().toString().substring(0, 8);

        Map<String, Object> extraDataMap = new HashMap<>();
        extraDataMap.put(ORDER_ID, orderId);
        extraDataMap.put("userId", order.getUser().getId());
        String extraData = PaymentUtil.encodeExtraData(objectMapper, extraDataMap);

        Map<String, Object> params = momoConfig.buildCreateOrderParams(requestId, orderIdMomo, amount,
                "Thanh toán đơn hàng #" + order.getOrderNumber(), extraData);

        String signature = momoHelper.createSignature(params);
        params.put("signature", signature);

        // Send request to MoMo
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

        String createOrderUrl = momoConfig.getEndPoint() + "/create";
        log.info("Sending request to MoMo URL: {}", createOrderUrl);

        ResponseEntity<String> response = restTemplate.postForEntity(createOrderUrl, entity, String.class);
        MomoCreateOrderResponseDto responseDto = objectMapper.readValue(response.getBody(),
                MomoCreateOrderResponseDto.class);

        // Lưu MoMo transaction payment
        createMomoTransaction(orderId, payment.getId(), orderIdMomo, requestId, order.getTotalAmount());

        Map<String, String> result = new HashMap<>();
        result.put("payUrl", responseDto.getPayUrl());
        result.put(ORDER_ID, String.valueOf(orderId));
        result.put("momoOrderId", responseDto.getOrderId());
        result.put(RESULT_CODE, responseDto.getResultCode());
        result.put(MESSAGE, responseDto.getMessage());

        log.info("MoMo order created successfully. Original orderId: {}, MoMo orderId: {}", orderId,
                responseDto.getOrderId());

        return result;

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

    @Override
    @Transactional
    public boolean handleIpnCallback(MomoIpnRequestDto request) {

        if (!momoHelper.verifySignature(request)) {
            log.error("Invalid signature for MoMo IPN: {}", request.getOrderId());
            return false;
        }
        Optional<MomoTransaction> momoTxnOpt = momoTransactionRepository.findByMomoOrderId(request.getOrderId());

        if (momoTxnOpt.isEmpty()) {
            log.error("MoMo transaction not found for orderId: {}", request.getOrderId());
            return false;
        }

        MomoTransaction momoTxn = momoTxnOpt.get();

        Optional<Payment> paymentOpt = paymentRepository.findById(momoTxn.getPaymentId());
        if (paymentOpt.isEmpty()) {
            log.error("Payment not found for id: {}", momoTxn.getPaymentId());
            return false;
        }

        Payment payment = paymentOpt.get();

        Optional<Order> orderOpt = orderRepository.findById(momoTxn.getOrderId());
        if (orderOpt.isEmpty()) {
            log.error("Order not found for id: {}", momoTxn.getOrderId());
            return false;
        }
        Order order = orderOpt.get();

        if ("0".equals(request.getResultCode())) {
            payment.setStatus(PaymentStatus.COMPLETED);
            momoTxn.setStatus(PaymentStatus.COMPLETED);
            order.setStatus(OrderStatus.COMPLETED);
        } else {
            payment.setStatus(PaymentStatus.CANCELLED);
            momoTxn.setStatus(PaymentStatus.CANCELLED);

            order.setStatus(OrderStatus.CANCELLED);
            log.warn("Payment failed for order: {} with result code: {}", request.getOrderId(),
                    request.getResultCode());
        }

        momoTxn.setMomoTransId(request.getTransId());
        momoTxn.setResultCode(request.getResultCode());
        momoTxn.setMessage(request.getMessage());

        paymentRepository.save(payment);
        momoTransactionRepository.save(momoTxn);
        orderRepository.save(order);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> handleRedirectCallback(Map<String, String> params) {
        Map<String, String> result = new HashMap<>();

        try {
            String orderId = params.get("orderId");
            String resultCode = params.get("resultCode");
            String message = params.get(MESSAGE);

            result.put("orderId", orderId);
            result.put("resultCode", resultCode);
            result.put(MESSAGE, message != null ? message : "");

            if ("0".equals(resultCode)) {
                result.put(STATUS, "success");
                log.info("MoMo payment successful for order: {}", orderId);
            } else {
                result.put(STATUS, "failed");
                log.warn("MoMo payment failed for order: {} with result code: {}", orderId, resultCode);
            }

        } catch (Exception e) {
            log.error("Error handling MoMo redirect callback: ", e);
            result.put(STATUS, "error");
            result.put("message", "Internal error");
        }

        return result;

    }

    private Payment getOrCreatePayment(Order order) {
        Optional<Payment> existingPaymentOpt = paymentRepository.findByOrderId(order.getId());

        if (existingPaymentOpt.isEmpty()) {
            return createPaymentRecord(order.getId());
        }

        Payment payment = existingPaymentOpt.get();

        if (payment.getStatus() == PaymentStatus.PENDING) {
            payment.setType(PaymentType.ONLINE_PAYMENT);
            payment.setGateway(PaymentGateway.MOMO);
            paymentRepository.save(payment);
        }

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new InvalidDataException(ErrorMessage.Payment.MOMO_PAYMENT_COMPLETED);
        }

        if (payment.getStatus() == PaymentStatus.EXPIRED || payment.getStatus() == PaymentStatus.CANCELLED) {
            payment.setStatus(PaymentStatus.PENDING);
            return paymentRepository.save(payment);
        }

        return payment;
    }

    private Payment createPaymentRecord(Long orderId) {

        Optional<Order> orderOtp = orderRepository.findById(orderId);
        if (orderOtp.isEmpty()) {
            throw new InvalidDataException("Order not found: " + orderId);
        }
        Order order = orderOtp.get();

        Payment payment = Payment.builder().amount(order.getTotalAmount()).gateway(PaymentGateway.MOMO)
                .type(PaymentType.ONLINE_PAYMENT).status(PaymentStatus.PENDING).order(order).build();

        return paymentRepository.save(payment);
    }

    private void createMomoTransaction(Long orderId, String paymentId, String momoOrderId, String requestId,
            Double amount) {
        MomoTransaction momoTxn = MomoTransaction.builder().orderId(orderId).paymentId(paymentId)
                .momoOrderId(momoOrderId).momoRequestId(requestId).amount(amount).status(PaymentStatus.PENDING).build();

        momoTransactionRepository.save(momoTxn);
    }
}
