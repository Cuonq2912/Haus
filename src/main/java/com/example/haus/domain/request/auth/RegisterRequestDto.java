package com.example.haus.domain.request.auth;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.validator.Email;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequestDto {

    @Schema(description = "Tên tài khoản", example = "user123")
    @NotBlank(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    String username;

    @Schema(description = "Mật khẩu", example = "User123@")
    @NotBlank(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}$", message = ErrorMessage.INVALID_FORMAT_PASSWORD)
    String password;

    @Schema(description = "Họ/Tên đệm", example = "Phạm Văn")
    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    String firstName;

    @Schema(description = "Tên", example = "A")
    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    String lastName;

    @Schema(description = "Email người dùng", example = "user@gmail.com")
    @NotBlank(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    @Email
    String email;
}
