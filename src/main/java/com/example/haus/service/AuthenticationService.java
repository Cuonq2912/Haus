package com.example.haus.service;

import com.example.haus.domain.dto.request.auth.*;
import com.example.haus.domain.dto.request.auth.otp.VerifyOtpRequestDto;
import com.example.haus.domain.dto.response.auth.LoginResponseDto;
import com.example.haus.domain.dto.response.auth.RefreshTokenResponseDto;
import com.example.haus.domain.dto.response.user.UserResponseDto;

public interface AuthenticationService {
    LoginResponseDto authentication(LoginRequestDto request);

//    LoginResponseDto loginWithGoogle(OAuth2GoogleRequestDto request) throws GeneralSecurityException, IOException;

    void logout(LogoutRequestDto request);

    RefreshTokenResponseDto refresh(RefreshTokenRequestDto request);

    void register(RegisterRequestDto request);

    UserResponseDto verifyOtpToRegister(VerifyOtpRequestDto request);

    void forgotPassword(ForgotPasswordRequestDto request);

    boolean verifyOtpToResetPassword(VerifyOtpRequestDto request);

    UserResponseDto resetPassword(ResetPasswordRequestDto request);

    String generateOtp();
}
