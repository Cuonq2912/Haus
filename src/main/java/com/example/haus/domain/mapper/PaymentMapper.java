package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.order.PaymentRequestDto;
import com.example.haus.domain.dto.response.payment.PaymentResponseDto;
import com.example.haus.domain.entity.product.payment.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PaymentMapper {

    PaymentResponseDto paymentToPaymentResponseDto(Payment payment);

    @Mapping(target = "gateway", source = "paymentGateway")
    @Mapping(target = "type", source = "paymentType")
    Payment paymentRequestDtoToPayment(PaymentRequestDto paymentRequestDto);
}
