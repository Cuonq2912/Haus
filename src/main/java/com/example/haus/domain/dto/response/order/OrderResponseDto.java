package com.example.haus.domain.dto.response.order;

import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.entity.product.OrderItem;
import com.example.haus.domain.entity.product.Promotion;
import com.example.haus.domain.entity.product.payment.Payment;
import com.example.haus.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponseDto {

    Long id;

    String orderNumber;

    Double shippingFee;

    Double totalAmount;

    OrderStatus status;

    LocalDate orderDate;

    LocalDate deliveryDate;
}
