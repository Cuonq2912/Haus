package com.example.haus.domain.dto.order;

import com.example.haus.constant.ErrorMessage;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderAllRequestDto {

    // order-item
    @NotEmpty(message = ErrorMessage.Order.ERR_PRODUCTS_EMPTY)
    List<OrderItemRequestDto> orderItems;

    // order
    OrderRequestDto order;
    // payment
    PaymentRequestDto payment;

}
