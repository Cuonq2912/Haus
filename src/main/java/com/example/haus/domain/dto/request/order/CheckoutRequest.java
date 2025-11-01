package com.example.haus.domain.dto.request.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckoutRequest {

    @NotEmpty(message = "Cart item IDs are required")
    List<Long> cartItemIds;

    @NotNull(message = "Address ID is required")
    Long addressId;

    @Size(max = 50, message = "Promotion code must not exceed 50 characters")
    String promotionCode;

    @NotNull(message = "Payment method is required")
    String paymentMethod;

    @Size(max = 500, message = "Note must not exceed 500 characters")
    String note;

    @Size(max = 100, message = "Recipient name must not exceed 100 characters")
    String recipientName;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    // validate
    String recipientPhone;
}
