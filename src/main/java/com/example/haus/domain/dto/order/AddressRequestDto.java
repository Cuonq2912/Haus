package com.example.haus.domain.dto.order;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.validator.ValidPromotionDates;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequestDto {
    @NotEmpty(message = ErrorMessage.Address.ERR_ID_EMPTY)
    @Schema(description = "Id địa chỉ", example = "1")
    Long id;

    @NotNull(message = ErrorMessage.Address.ERR_IS_SELECTED_NULL)
    @Schema(example = "true")
    Boolean isSelected;
}
