package com.example.haus.domain.request.user.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateAddressRequestDto {
    @Schema(description = "Quốc gia", example = "Việt nam")
    private String country;

    @Schema(description = "Tỉnh/Thành phố", example = "Hà Nội")
    private String city;

    @Schema(description = "Quận/Huyện", example = "Minh Khai")
    private String district;

    @Schema(description = "Xã/Thôn", example = "Xuân Phương")
    private String commune;

    @Schema(description = "Địa chỉ chi tiết", example = "địa chỉ")
    private String detailAddress;
}
