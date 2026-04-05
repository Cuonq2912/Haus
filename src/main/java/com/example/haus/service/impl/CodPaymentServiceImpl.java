package com.example.haus.service.impl;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.dto.request.product.CodPaymentRequestDto;
import com.example.haus.domain.dto.response.product.CodPaymentResponseDto;
import com.example.haus.domain.entity.product.Order;
import com.example.haus.domain.entity.product.payment.Payment;
import com.example.haus.domain.entity.product.payment.PaymentStatus;
import com.example.haus.domain.entity.product.payment.PaymentType;
import com.example.haus.domain.entity.user.Role;
import com.example.haus.domain.entity.user.User;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.OrderRepository;
import com.example.haus.repository.PaymentRepository;
import com.example.haus.repository.UserRepository;
import com.example.haus.service.CodPaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "COD-PAYMENT-SERVICE")
public class CodPaymentServiceImpl implements CodPaymentService {

    OrderRepository orderRepository;
    PaymentRepository paymentRepository;
    UserRepository userRepository;

    @Override
    @Transactional
    public CodPaymentResponseDto processCodPayment(CodPaymentRequestDto request, String username) {

        Order order = getAccessibleOrder(request.getOrderId(), username);

        if (!order.getPayment().getType().equals(PaymentType.COD)) {
            throw new InvalidDataException("Payment type invalid");
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new InvalidDataException(ErrorMessage.Payment.COD_ORDER_ALREADY_COMPLETED);
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidDataException(ErrorMessage.Payment.COD_ORDER_CANCELLED);
        }

        Payment payment = getOrCreateCodPayment(order);

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        return CodPaymentResponseDto.builder().orderId(order.getId()).orderNumber(order.getOrderNumber())
                .totalAmount(order.getTotalAmount()).orderStatus(order.getStatus()).paymentStatus(payment.getStatus())
                .paymentId(payment.getId()).message(request.getNote()).build();
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

    private Payment getOrCreateCodPayment(Order order) {
        Optional<Payment> existingPaymentOpt = paymentRepository.findByOrderId(order.getId());

        if (existingPaymentOpt.isPresent()) {
            Payment payment = existingPaymentOpt.get();

            if (payment.getStatus() == PaymentStatus.PENDING) {
                payment.setType(PaymentType.COD);
                payment.setGateway(null);
                paymentRepository.save(payment);
            }

            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                throw new InvalidDataException(ErrorMessage.Payment.COD_PAYMENT_ALREADY_COMPLETED);
            }

            if (payment.getStatus() == PaymentStatus.CANCELLED || payment.getStatus() == PaymentStatus.EXPIRED) {
                payment.setStatus(PaymentStatus.PENDING);
                return paymentRepository.save(payment);
            }

            return payment;
        }

        return createCodPaymentRecord(order);
    }

    private Payment createCodPaymentRecord(Order order) {
        Payment payment = Payment.builder().amount(order.getTotalAmount()).gateway(null).type(PaymentType.COD)
                .status(PaymentStatus.PENDING).order(order).build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Created new COD payment record: {} for order: {}", savedPayment.getId(), order.getId());

        return savedPayment;
    }
}
