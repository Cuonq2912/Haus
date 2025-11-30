package com.example.haus.domain.dto.response.statistic;

import com.example.haus.domain.dto.response.product.OrderResponseDto;
import com.example.haus.domain.dto.response.product.ProductStatisticResponseDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticResponseDto {

    Map<String, RevenueDetailResponseDto> revenue;

    ProductStatisticResponseDto bestSellers;

    Map<String, Double> saleGraph;

    OrderResponseDto recentOrder;
}
