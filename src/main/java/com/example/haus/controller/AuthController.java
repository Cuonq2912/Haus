package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.dto.request.auth.*;
import com.example.haus.domain.dto.request.auth.otp.VerifyOtpRequestDto;
import com.example.haus.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthenticationService authenticationService;

    @Operation(
            summary = "Đăng nhập tài khoản",
            description = "Dùng để đăng nhập tài khoản"
    )
    @PostMapping(UrlConstant.Auth.LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseUtil.success(
                SuccessMessage.Auth.LOGIN_SUCCESS,
                authenticationService.authentication(loginRequestDto)
        );
    }

    @Operation(
            summary = "Đăng xuất tài khoản",
            description = "Dùng để đăng xuất tài khoản"
    )
    @PostMapping(UrlConstant.Auth.LOGOUT)
    public ResponseEntity<?> logout(@Valid @RequestBody LogoutRequestDto logoutRequestDto) {
        authenticationService.logout(logoutRequestDto);
        return ResponseUtil.success(HttpStatus.NO_CONTENT, SuccessMessage.Auth.LOGOUT_SUCCESS, null);
    }

    @Operation(
            summary = "Làm mới token",
            description = "Dùng để cấp lại token"
    )
    @PostMapping(UrlConstant.Auth.REFRESH_TOKEN)
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        return ResponseUtil.success(
                SuccessMessage.Auth.REFRESH_TOKEN_SUCCESS,
                authenticationService.refresh(refreshTokenRequestDto)
        );
    }

    @Operation(
            summary = "Đăng kí tài khoản",
            description = "Dùng để đăng kí tài khoản"
    )
    @PostMapping(UrlConstant.Auth.REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        authenticationService.register(registerRequestDto);
        return ResponseUtil.success(HttpStatus.CREATED, SuccessMessage.Auth.REGISTER_SEND_OTP_SUCCESS, null);
    }

    @Operation(
            summary = "Xác thực OTP",
            description = "Dùng để xác thực OTP sau khi yêu cầu đăng kí tài khoản"
    )
    @PostMapping(UrlConstant.Auth.VERIFY_OTP)
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyOtpRequestDto verifyOtpRequestDto) {
        return ResponseUtil.success(
                HttpStatus.CREATED,
                SuccessMessage.Auth.VERIFY_OTP_REGISTER_SUCCESS,
                authenticationService.verifyOtpToRegister(verifyOtpRequestDto)
        );
    }

    @Operation(
            summary = "Quên mật khẩu",
            description = "Dùng để lấy lại mật khẩu"
    )
    @PostMapping(UrlConstant.Auth.FORGOT_PASSWORD)
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto) {
        authenticationService.forgotPassword(forgotPasswordRequestDto);
        return ResponseUtil.success(HttpStatus.ACCEPTED, SuccessMessage.Auth.FORGOT_PASSWORD_SUCCESS, null);
    }

    @Operation(
            summary = "Xác thực OTP",
            description = "Dùng để xác thực OTP sau khi yêu cầu lấy lại mật khẩu"
    )
    @PostMapping(UrlConstant.Auth.VERIFY_OTP_TO_RESET_PASSWORD)
    public ResponseEntity<?> verifyToResetPassword(@Valid @RequestBody VerifyOtpRequestDto request) {
        return ResponseUtil.success(
                HttpStatus.OK,
                authenticationService.verifyOtpToResetPassword(request) ? SuccessMessage.Auth.VERIFY_OTP_TO_RESET_PASSWORD_SUCCESS : null);
    }

    @Operation(
            summary = "Đặt lại mật khẩu",
            description = "Dùng để đặt lại mật khẩu sau khi đã nhập được OTP"
    )
    @PostMapping(UrlConstant.Auth.RESET_PASSWORD)
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        return ResponseUtil.success(
                SuccessMessage.Auth.RESET_PASSWORD_SUCCESS,
                authenticationService.resetPassword(request)
        );
    }
}
