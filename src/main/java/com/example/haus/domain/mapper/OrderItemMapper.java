package com.example.haus.domain.mapper;


import com.example.haus.domain.dto.order.OrderItemRequestDto;
import com.example.haus.domain.entity.product.OrderItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface OrderItemMapper {

    OrderItem productVariationInOrderRequestDtoToOrderItem (OrderItemRequestDto requestDto);

    OrderItem orderItemRequestDtoToOrderItem(OrderItemRequestDto orderItemRequestDto);
}
