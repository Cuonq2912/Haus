package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.order.OrderRequestDto;
import com.example.haus.domain.dto.response.product.OrderItemResponseDto;
import com.example.haus.domain.dto.response.product.OrderResponseDto;
import com.example.haus.domain.dto.response.statistic.RecentOrderResponseDto;
import com.example.haus.domain.entity.product.Order;
import com.example.haus.domain.entity.product.OrderItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    uses = { AddressMapper.class, OrderItemMapper.class, MediaMapper.class }
)
public interface OrderMapper {
    @Mapping(target = "recipientInfo", source = "shippingAddress")
    @Mapping(target = "products", source = "orderItems")
    OrderResponseDto orderToOrderResponseDto(Order order);

    @Mapping(target = "recipientInfo", source = "shippingAddress")
    @Mapping(target = "products", source = "orderItems")
    OrderResponseDto orderToOrderResponse(Order order);

    @Mapping(target = "customerName", source = "recipientName")
    RecentOrderResponseDto orderToRecentOrderResponseDto(Order order);

    @Mappings({
            @Mapping(target = "orderNumber", source = "orderNumber"),
            @Mapping(target = "shippingFee", source = "shippingFee"),
            @Mapping(target = "totalAmount", source = "totalAmount"),
            @Mapping(target = "orderDate", source = "orderDate")
    })
    Order orderRequestDtoToOrder(OrderRequestDto orderRequestDto);
}
