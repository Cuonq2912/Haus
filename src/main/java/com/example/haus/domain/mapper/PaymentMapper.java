package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.response.payment.PaymentResponseDto;
import com.example.haus.domain.entity.product.payment.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PaymentMapper {

    PaymentResponseDto paymentToPaymentResponseDto(Payment payment);
}
