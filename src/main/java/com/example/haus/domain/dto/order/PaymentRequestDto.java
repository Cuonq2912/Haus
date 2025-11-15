package com.example.haus.domain.dto.order;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.entity.product.payment.PaymentGateway;
import com.example.haus.domain.entity.product.payment.PaymentType;
import com.example.haus.domain.validator.EnumValue;
import com.example.haus.domain.validator.ValidPromotionDates;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequestDto {

    @EnumValue(name = "paymentGateway", enumClass = PaymentGateway.class)
    @Schema(example = "VNPAY")
    PaymentGateway paymentGateway;

    @EnumValue(name = "paymentType", enumClass = PaymentType.class)
    @Schema(example = "ONLINE_PAYMENT")
    PaymentType paymentType;
}
