package com.example.haus.domain.request.user.profile;

import com.example.haus.constant.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmPasswordRequestDto {

    @Schema(description = "Mật khẩu xác nhận", example = "User123@")
    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    String password;

    @Schema(description = "Thông tin profile cần cập nhật")
    @Valid
    UpdatePersonalInformationRequestDto profileData;
}
