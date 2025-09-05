package com.example.haus.domain.dto.request.user.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePasswordRequestDto {

    @Schema(description = "Mật khẩu hiện tại", example = "currentPassword@123")
    String currentPassword;

    @Schema(description = "Mật khẩu mới", example = "newPassword@123")
    String newPassword;
}
