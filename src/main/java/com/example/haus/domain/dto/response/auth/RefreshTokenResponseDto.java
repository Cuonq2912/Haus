package com.example.haus.domain.dto.response.auth;

import com.example.haus.constant.CommonConstant;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefreshTokenResponseDto {

    String tokenType = CommonConstant.BEARER_TOKEN;

    String accessToken;

    String refreshToken;

}
