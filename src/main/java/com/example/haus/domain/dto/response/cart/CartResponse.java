package com.example.haus.domain.dto.response.cart;

import com.example.haus.domain.entity.product.CartItem;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    List<CartItemResponse> items;

    @UpdateTimestamp
    LocalDateTime lastUpdated;
}
