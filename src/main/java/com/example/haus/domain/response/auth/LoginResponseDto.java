package com.example.haus.domain.response.auth;

import com.example.haus.constant.CommonConstant;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class LoginResponseDto {

    String tokenType = CommonConstant.BEARER_TOKEN;

    String userId;

    String accessToken;

    String refreshToken;

//    Boolean isDeleted;
//
//    Boolean canRecovery;
//
//    long dayRecoveryRemaining;

}
