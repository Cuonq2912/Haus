package com.example.haus.domain.dto.order;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.validator.ValidPromotionDates;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequestDto {

    @NotEmpty(message = ErrorMessage.Order.ERR_ORDER_NUMBER_EMPTY)
    @Schema(example = "ORDER-1")
    String orderNumber;

    @NotEmpty(message = ErrorMessage.Order.ERR_SHIPPING_FEE_EMPTY)
    @Min(value = 1, message = ErrorMessage.Order.ERR_NUMBER_INVALID)
    @Schema(example = "30000")
    Double shippingFee;

    @NotEmpty(message = ErrorMessage.Order.ERR_TOTAL_AMOUNT_EMPTY)
    @Min(value = 1, message = ErrorMessage.Order.ERR_NUMBER_INVALID)
    @Schema(example = "50000")
    Double totalAmount;

    @NotEmpty(message = ErrorMessage.Order.ERR_ORDER_DATE_EMPTY)
    LocalDate orderDate;

    @NotEmpty(message = ErrorMessage.Order.ERR_ADDRESS_ID_EMPTY)
    @Min(value = 1, message = ErrorMessage.Order.ERR_NUMBER_INVALID)
    @Schema(example = "[{\"id\": 1, \"isSelected\": true}]")
    List<AddressRequestDto> addresses;

    @NotEmpty(message = ErrorMessage.Order.ERR_PROMOTION_ID_EMPTY)
    @Min(value = 1, message = ErrorMessage.Order.ERR_NUMBER_INVALID)
    @Schema(example = "1")
    Long promotionId;


}
