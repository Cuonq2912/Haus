package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.response.product.OrderResponseDto;
import com.example.haus.domain.entity.product.Order;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface OrderMapper {

    OrderResponseDto orderToOrderResponseDto(Order order);

    OrderResponseDto orderToOrderResponse(Order order);

}
