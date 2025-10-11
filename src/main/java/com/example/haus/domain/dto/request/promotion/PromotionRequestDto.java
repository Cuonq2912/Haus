package com.example.haus.domain.dto.request.promotion;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.promotion.PromotionStatus;
import com.example.haus.constant.promotion.PromotionType;
import com.example.haus.domain.entity.product.Promotion;
import com.example.haus.domain.validator.PositiveOrNull;
import com.example.haus.domain.validator.ValidPromotionDates;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ValidPromotionDates
public class PromotionRequestDto {

    @NotBlank(message = ErrorMessage.Promotion.ERR_PROMOTION_CODE_NOT_BLANK)
    @Size(min = 6, max = 15, message = ErrorMessage.Promotion.ERR_PROMOTION_CODE_LENGTH)
    String promotionCode;
    @NotNull(message = ErrorMessage.Promotion.ERR_PROMOTION_DESCRIPTION_NOT_NULL)
    String description;

    @NotNull(message = ErrorMessage.Promotion.ERR_PROMOTION_TYPE_NOT_NULL)
    PromotionType type;

    @NotNull(message = ErrorMessage.Promotion.ERR_PROMOTION_STATUS_NOT_NULL)
    PromotionStatus status;
    @NotNull(message = ErrorMessage.Promotion.ERR_PROMOTION_START_DATE_NOT_NULL)
    LocalDate startDate;
    @NotNull(message = ErrorMessage.Promotion.ERR_PROMOTION_END_DATE_NOT_NULL)
    LocalDate endDate;

    @PositiveOrNull
    Long minPriceOrder;
    @PositiveOrNull
    Long maxPriceOrder;

    @NotNull(message = ErrorMessage.Promotion.ERR_PROMOTION_DISCOUNT_PERCENT_NOT_NULL)
    @Min(value = 0, message = ErrorMessage.Promotion.ERR_PROMOTION_DISCOUNT_PERCENT_INVALID)
    @Max(value = 100, message = ErrorMessage.Promotion.ERR_PROMOTION_DISCOUNT_PERCENT_INVALID)
    Float discountPercent;

    Long categoryId;
}
