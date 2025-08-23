package com.example.haus.domain.request.auth;

import com.example.haus.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogoutRequestDto {

    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    String token;

}
