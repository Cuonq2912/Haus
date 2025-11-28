package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.order.OrderRequestDto;
import com.example.haus.domain.dto.response.product.OrderResponseDto;
import com.example.haus.domain.entity.product.Order;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    uses = { AddressMapper.class }
)
public interface OrderMapper {
    @Mapping(target = "user", source = "shippingAddress")
    OrderResponseDto orderToOrderResponseDto(Order order);

    @Mapping(target = "user", source = "shippingAddress")
    OrderResponseDto orderToOrderResponse(Order order);

    @Mappings({
            @Mapping(target = "orderNumber", source = "orderNumber"),
            @Mapping(target = "shippingFee", source = "shippingFee"),
            @Mapping(target = "totalAmount", source = "totalAmount"),
            @Mapping(target = "orderDate", source = "orderDate")
    })
    Order orderRequestDtoToOrder(OrderRequestDto orderRequestDto);
}
