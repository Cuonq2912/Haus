package com.example.haus.domain.dto.order;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.validator.ValidPromotionDates;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemRequestDto {

    @NotEmpty(message = ErrorMessage.Order.ERR_PRODUCT_VARIATION_ID_EMPTY)
    Long productVariationId;

    @NotEmpty(message = ErrorMessage.Order.ERR_QUANTITY_EMPTY)
    @Min(value = 1, message = ErrorMessage.Order.ERR_NUMBER_INVALID)
    Integer quantity;

    @NotEmpty(message = ErrorMessage.Order.ERR_PRICE_AT_SALE_EMPTY)
    @Min(value = 1, message = ErrorMessage.Order.ERR_NUMBER_INVALID)
    Double priceAtSale;

}
