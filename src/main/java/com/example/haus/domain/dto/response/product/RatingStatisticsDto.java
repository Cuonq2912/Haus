package com.example.haus.domain.dto.response.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatingStatisticsDto {

    Long productId;
    String productName;

    Long totalReviews;
    Double averageRating;

    Long rating5Count;
    Long rating4Count;
    Long rating3Count;
    Long rating2Count;
    Long rating1Count;

    Double rating5Percentage;
    Double rating4Percentage;
    Double rating3Percentage;
    Double rating2Percentage;
    Double rating1Percentage;
}
