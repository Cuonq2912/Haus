package com.example.haus.domain.dto.request.address;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.validator.PhoneNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequestDto {

    @NotEmpty(message = ErrorMessage.Address.ERR_RECIPIENT_NAME_EMPTY)
    @Schema(example = "Bùi Đức Quân")
    String recipientName;

    @NotEmpty(message = ErrorMessage.Address.ERR_PHONE_NUMBER_EMPTY)
    @Schema(example = "0822091972")
    @PhoneNumber()
    String phoneNumber;

    @NotEmpty(message = ErrorMessage.Address.ERR_COUNTRY_EMPTY)
    @Schema(example = "Việt Nam")
    String country;

    @NotEmpty(message = ErrorMessage.Address.ERR_CITY_EMPTY)
    @Schema(example = "Hà Nội")
    String city;

    @NotEmpty(message = ErrorMessage.Address.ERR_DISTRICT_EMPTY)
    @Schema(example = "Hoàng Mai")
    String district;

    @NotEmpty(message = ErrorMessage.Address.ERR_COMMUNE_EMPTY)
    @Schema(example = "Xuân Phương")
    String commune;

    @NotNull(message = ErrorMessage.Address.ERR_DETAIL_ADDRESS_NULL)
    @Schema(example = "Số nhà 1, ngõ 1, Xuân Phương, Hoàng Mai, Hà Nội")
    String detailAddress;

}
