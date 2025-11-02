package com.example.haus.domain.dto.order;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.entity.product.payment.PaymentGateway;
import com.example.haus.domain.entity.product.payment.PaymentType;
import com.example.haus.domain.validator.EnumValue;
import com.example.haus.domain.validator.ValidPromotionDates;
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
    PaymentGateway paymentGateway;

    @EnumValue(name = "paymentType", enumClass = PaymentType.class)
    @NotEmpty(message = ErrorMessage.Order.ERR_PAYMENT_TYPE_EMPTY)
    PaymentType paymentType;
}
