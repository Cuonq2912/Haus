    package com.example.haus.domain.dto.response.cart;

    import lombok.*;
    import lombok.experimental.FieldDefaults;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class CartItemResponse {

        Long productVariationId;

        String productName;

        Integer quantity;

    }
