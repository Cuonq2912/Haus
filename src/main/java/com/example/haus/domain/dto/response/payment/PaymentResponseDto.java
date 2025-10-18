package com.example.haus.domain.dto.response.payment;

import com.example.haus.domain.entity.product.Order;
import com.example.haus.domain.entity.product.payment.PaymentGateway;
import com.example.haus.domain.entity.product.payment.PaymentStatus;
import com.example.haus.domain.entity.product.payment.PaymentType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponseDto {

    Long amount;

    PaymentGateway gateway;

    PaymentType type;

    PaymentStatus status;

    Date expireAt;
}
