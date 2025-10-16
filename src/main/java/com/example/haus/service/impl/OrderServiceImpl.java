package com.example.haus.service.impl;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.response.product.OrderResponseDto;
import com.example.haus.domain.entity.product.Order;
import com.example.haus.domain.entity.product.Promotion;
import com.example.haus.domain.entity.product.payment.Payment;
import com.example.haus.domain.entity.product.payment.PaymentStatus;
import com.example.haus.domain.entity.user.User;
import com.example.haus.domain.mapper.OrderMapper;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.OrderRepository;
import com.example.haus.service.OrderService;
import com.example.haus.util.PaginationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "ORDER-SERVICE")
public class OrderServiceImpl implements OrderService {

    OrderRepository orderRepository;

    OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDto<OrderResponseDto> getAllOrders(PaginationRequestDto paginationRequest, String status) {

        Pageable pageable = PageRequest.of(
                paginationRequest.getPageNum(),
                paginationRequest.getPageSize());

        Page<Order> orderPage;

        if (status != null && !status.trim().isEmpty()) {
            OrderStatus orderStatus;
            try {
                orderStatus = OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException(ErrorMessage.Order.ERR_INVALID_ORDER_STATUS);
            }
            orderPage = orderRepository.findByStatus(orderStatus, pageable);
        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        List<OrderResponseDto> orderResponseList = orderPage.getContent().stream()
                .map(this::convertToOrderResponseDto)
                .toList();

        return PaginationUtil.createPaginationResponse(orderPage, paginationRequest, orderResponseList);
    }

    @Override
    public OrderResponseDto getOrderById(Long id) {

        if (id == null || id <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Order.ERR_ORDER_NOT_EXISTED));

        return orderMapper.orderToOrderResponse(order);

    }

    @Override
    @Transactional
    public OrderResponseDto updateStatusOrderById(Long id, String status) {
        if (id == null || id <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Order.ERR_ORDER_NOT_EXISTED));

        if (status == null || status.trim().isEmpty()) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new InvalidDataException(ErrorMessage.Order.ERR_INVALID_ORDER_STATUS);
        }

        order.setStatus(orderStatus);

        Payment payment = order.getPayment();
        if (payment != null) {
            switch (orderStatus) {
                case PENDING, PROCESSING, DELIVERED, CONFIRMED -> payment.setStatus(PaymentStatus.PENDING);
                case COMPLETED, RETURNED -> payment.setStatus(PaymentStatus.COMPLETED);
                case CANCELLED -> payment.setStatus(PaymentStatus.CANCELLED);
                case REFUNDED -> payment.setStatus(PaymentStatus.REFUNDED);
                default -> {
                    throw new InvalidDataException(ErrorMessage.Payment.STATUS_IS_NOT_SUPPORT);
                }
            }
        }

        Order updatedOrder = orderRepository.save(order);

        return convertToOrderResponseDto(updatedOrder);
    }

    private OrderResponseDto convertToOrderResponseDto(Order order) {
        OrderResponseDto dto = OrderResponseDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .shippingFee(order.getShippingFee())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .deliveryDate(order.getDeliveryDate())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();

        if (order.getUser() != null) {
            dto.setUser(convertToUserInfo(order.getUser()));
        }

        if (order.getPromotion() != null) {
            dto.setPromotion(convertToPromotionInfo(order.getPromotion()));
        }

        if (order.getPayment() != null) {
            dto.setPayment(convertToPaymentInfo(order.getPayment()));
        }

        return dto;
    }

    private OrderResponseDto.UserInfo convertToUserInfo(User user) {
        return OrderResponseDto.UserInfo.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .build();
    }

    private OrderResponseDto.PromotionInfo convertToPromotionInfo(Promotion promotion) {
        return OrderResponseDto.PromotionInfo.builder()
                .code(promotion.getPromotionCode())
                .discountPercent(promotion.getDiscountPercent() != null
                        ? promotion.getDiscountPercent().intValue()
                        : null)
                .build();
    }

    private OrderResponseDto.PaymentInfo convertToPaymentInfo(Payment payment) {
        return OrderResponseDto.PaymentInfo.builder()
                .amount(payment.getAmount())
                .type(payment.getType())
                .status(payment.getStatus())
                .build();
    }
}
