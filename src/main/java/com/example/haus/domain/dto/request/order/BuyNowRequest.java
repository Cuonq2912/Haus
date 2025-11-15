package com.example.haus.domain.dto.request.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuyNowRequest {

    @NotNull(message = "Product variation ID is required")
    Long productVariationId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity;

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
    String recipientPhone;
}
