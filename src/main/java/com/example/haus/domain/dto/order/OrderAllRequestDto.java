package com.example.haus.domain.dto.order;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.entity.address.Address;
import com.example.haus.domain.entity.product.payment.PaymentGateway;
import com.example.haus.domain.entity.product.payment.PaymentType;
import com.example.haus.domain.validator.EnumValue;
import com.example.haus.domain.validator.ValidPromotionDates;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderAllRequestDto {

    //order-item
    @NotEmpty(message = ErrorMessage.Order.ERR_PRODUCTS_EMPTY)
    List<OrderItemRequestDto> orderItems;

    //order
    OrderRequestDto order;
    //payment
    PaymentRequestDto payment;

}
