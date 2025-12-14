package com.example.haus.domain.dto.response.statistic;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BestSellerRow {
    Long productId;
    Long soldQuantity;
}
