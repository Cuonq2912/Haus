package com.example.haus.domain.dto.response.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class LoginResponseDto {

    String tokenType;

    String userId;

    String role;

    String accessToken;

    String refreshToken;

//    Boolean isDeleted;
//
//    Boolean canRecovery;
//
//    long dayRecoveryRemaining;

}
