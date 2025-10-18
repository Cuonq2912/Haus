package com.example.haus.domain.dto.response.product;

import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.entity.product.payment.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Response DTO cho thanh toán COD")
public class CodPaymentResponseDto {

    Long orderId;

    String orderNumber;

    Double totalAmount;

    OrderStatus orderStatus;

    PaymentStatus paymentStatus;

    String paymentId;

    String message;
}
