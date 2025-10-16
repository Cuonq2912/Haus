package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.response.product.OrderResponseDto;
import com.example.haus.domain.entity.product.Order;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

    OrderResponseDto orderToOrderResponse(Order order);

}
