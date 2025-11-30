package com.example.haus.domain.dto.response.statistic;

import com.google.api.client.util.DateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueDetailResponseDto {
    Double totalOrder;
    Double percentIncrease;
}
