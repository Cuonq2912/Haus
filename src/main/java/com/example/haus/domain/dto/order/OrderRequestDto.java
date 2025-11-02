package com.example.haus.domain.dto.order;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.validator.ValidPromotionDates;
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
    String orderNumber;

    @NotEmpty(message = ErrorMessage.Order.ERR_SHIPPING_FEE_EMPTY)
    @Min(value = 1, message = ErrorMessage.Order.ERR_NUMBER_INVALID)
    Double shippingFee;

    @NotEmpty(message = ErrorMessage.Order.ERR_TOTAL_AMOUNT_EMPTY)
    @Min(value = 1, message = ErrorMessage.Order.ERR_NUMBER_INVALID)
    Double totalAmount;

    @NotEmpty(message = ErrorMessage.Order.ERR_ORDER_DATE_EMPTY)
    LocalDate orderDate;

    @NotEmpty(message = ErrorMessage.Order.ERR_ADDRESS_ID_EMPTY)
    @Min(value = 1, message = ErrorMessage.Order.ERR_NUMBER_INVALID)
    List<AddressRequestDto> addresses;

    @NotEmpty(message = ErrorMessage.Order.ERR_PROMOTION_ID_EMPTY)
    @Min(value = 1, message = ErrorMessage.Order.ERR_NUMBER_INVALID)
    Long promotionId;


}
