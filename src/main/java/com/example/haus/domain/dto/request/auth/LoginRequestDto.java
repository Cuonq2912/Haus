package com.example.haus.domain.dto.request.auth;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.validator.Email;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequestDto {

    @Schema(description = "Email người dùng", example = "quanducbui2017@gmail.com")
    @NotBlank(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    @Email
    String email;

    @Schema(description = "Mật khẩu", example = "Quankane1905@@")
    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    String password;

}
