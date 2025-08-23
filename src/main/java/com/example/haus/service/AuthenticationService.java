package com.example.haus.service;

import com.example.haus.domain.request.auth.*;
import com.example.haus.domain.request.auth.otp.VerifyOtpRequestDto;
import com.example.haus.domain.response.auth.LoginResponseDto;
import com.example.haus.domain.response.auth.RefreshTokenResponseDto;
import com.example.haus.domain.response.user.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface AuthenticationService {
    LoginResponseDto authentication(LoginRequestDto request);

//    LoginResponseDto loginWithGoogle(OAuth2GoogleRequestDto request) throws GeneralSecurityException, IOException;

    void logout(LogoutRequestDto request);

    RefreshTokenResponseDto refresh(RefreshTokenResponseDto request);

    void register(RegisterRequestDto request);

    UserResponseDto verifyOtpToRegister(VerifyOtpRequestDto request);

    void forgotPassword(ForgotPasswordRequestDto request);

    boolean verifyOtpToResetPassword(VerifyOtpRequestDto request);

    UserResponseDto resetPassword(ResetPasswordRequestDto request);

    String generateOtp();
}
