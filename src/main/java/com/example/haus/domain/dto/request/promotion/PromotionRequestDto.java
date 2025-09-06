package com.example.haus.domain.dto.request.promotion;

import com.example.haus.constant.promotion.PromotionStatus;
import com.example.haus.constant.promotion.PromotionType;
import com.example.haus.domain.entity.product.Promotion;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionRequestDto {

    String promotionCode; // bắt buộc, duy nhất
    String description;   // mô tả

    PromotionType type;   // ENUM: ORDER / CATEGORY

    PromotionStatus status; // ENUM: ACTIVE / INACTIVE / EXPIRED
    LocalDate startDate;
    LocalDate endDate;

    // --- Chỉ áp dụng cho loại khuyến mãi theo đơn hàng (only order)---
    Float minPriceOrder;  // giá trị tối thiểu
    Float maxPriceOrder;  // giá trị tối đa


    Float discountPercent; // % giảm giá

    // --- Chỉ áp dụng cho loại khuyến mãi theo danh mục ---
    Long categoryId; // nếu type = CATEGORY
}
