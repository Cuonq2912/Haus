package com.example.haus.service.impl;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.dto.response.invoice.InvoiceItemDto;
import com.example.haus.domain.dto.response.invoice.InvoiceResponseDto;
import com.example.haus.domain.entity.product.Order;
import com.example.haus.domain.entity.product.payment.Payment;
import com.example.haus.domain.entity.user.User;
import com.example.haus.domain.mapper.*;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.OrderRepository;
import com.example.haus.service.StatisticsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PROMOTION-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsServiceImpl implements StatisticsService {

    OrderRepository orderRepository;
    MediaMapper mediaMapper;
    OrderMapper orderMapper;
    PromotionMapper promotionMapper;
    UserMapper userMapper;
    PaymentMapper paymentMapper;

    @Override
    public InvoiceResponseDto getStatistics(Long orderId) {
        Order order = orderRepository.findOrderDetailsForInvoice(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Order.ERR_ORDER_NOT_EXISTED));

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new ResourceNotFoundException(ErrorMessage.Order.ERR_ORDER_NOT_COMPLETED);
        }

        User user = order.getUser();
        Payment payment = order.getPayment();

        List<InvoiceItemDto> itemDtos = order.getOrderItems().stream()
                .map(item -> {
                    Double total = item.getQuantity() * item.getPriceAtSale();

                    return InvoiceItemDto.builder()
                            //Product
                            .productId(item.getProductVariation().getProduct().getId())
                            .productCode(item.getProductVariation().getProduct().getProductCode())
                            .productName(item.getProductVariation().getProduct().getProductName())
                            .description(item.getProductVariation().getProduct().getDescription())

                            //Product variant
                            .productVariationId(item.getProductVariation().getId())
                            .inventoryQuantity(item.getQuantity())
                            .total(total)
                            .color(item.getProductVariation().getColor())
                            .size(item.getProductVariation().getSize())
                            .price(item.getPriceAtSale())
                            .media(mediaMapper.mediaToMediaResponse(item.getProductVariation().getMedia()))
                            .build();
                })
                .toList();

        InvoiceResponseDto.InvoiceResponseDtoBuilder builder = InvoiceResponseDto.builder();

        builder.responseDto(orderMapper.orderToOrderResponseDto(order));
        builder.user(userMapper.userToUserResponseDto(user));
        builder.payment(paymentMapper.paymentToPaymentResponseDto(payment));

        if (order.getPromotion() != null) {
            builder.promotion(promotionMapper.promotionToPromotionResponseDto(order.getPromotion()));
        }

        builder.items(itemDtos);

        return builder.build();
    }
}
