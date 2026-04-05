package com.example.haus.domain.dto.response.cart;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponseDto {
    List<ProductInCartResponseDto> cartItems;

    @Builder.Default
    LocalDateTime lastUpdated = LocalDateTime.now();
}
