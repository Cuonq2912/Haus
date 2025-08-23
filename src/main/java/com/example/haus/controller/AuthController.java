package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.request.auth.*;
import com.example.haus.domain.request.auth.otp.VerifyOtpRequestDto;
import com.example.haus.domain.response.auth.LoginResponseDto;
import com.example.haus.domain.response.auth.RefreshTokenResponseDto;
import com.example.haus.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
        return ResponseUtil.success("Login successful", authenticationService.authentication(loginRequestDto));
    }

    @Operation(
            summary = "Đăng xuất tài khoản",
            description = "Dùng để đăng xuất tài khoản"
    )
    @PostMapping(UrlConstant.Auth.LOGOUT)
    public ResponseEntity<?> logout(@Valid @RequestBody LogoutRequestDto logoutRequestDto) {
        authenticationService.logout(logoutRequestDto);
        return ResponseUtil.success("Logout successful", "Đăng xuất tài khoản thành công");
    }

    @Operation(
            summary = "Làm mới token",
            description = "Dùng để cấp lại token"
    )
    @PostMapping(UrlConstant.Auth.REFRESH_TOKEN)
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenResponseDto refreshTokenResponseDto) {
        return ResponseUtil.success("Take refresh token successful", authenticationService.refresh(refreshTokenResponseDto));
    }

    @Operation(
            summary = "Đăng kí tài khoản",
            description = "Dùng để đăng kí tài khoản"
    )
    @PostMapping(UrlConstant.Auth.REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        authenticationService.register(registerRequestDto);
        return ResponseUtil.success("Register successful","Đăng ký tài khoản thành công");
    }

    @Operation(
            summary = "Xác thực OTP",
            description = "Dùng để xác thực OTP sau khi yêu cầu đăng kí tài khoản"
    )
    @PostMapping(UrlConstant.Auth.VERIFY_OTP)
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyOtpRequestDto verifyOtpRequestDto) {
        return ResponseUtil.success("Verify successful",authenticationService.verifyOtpToRegister(verifyOtpRequestDto));
    }

    @Operation(
            summary = "Quên mật khẩu",
            description = "Dùng để lấy lại mật khẩu"
    )
    @PostMapping(UrlConstant.Auth.FORGOT_PASSWORD)
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto) {
        authenticationService.forgotPassword(forgotPasswordRequestDto);
        return ResponseUtil.success("Forgot password successful", null);
    }

    @Operation(
            summary = "Xác thực OTP",
            description = "Dùng để xác thực OTP sau khi yêu cầu lấy lại mật khẩu"
    )
    @PostMapping(UrlConstant.Auth.VERIFY_OTP_TO_RESET_PASSWORD)
    public ResponseEntity<?> verifyToResetPassword(@Valid @RequestBody VerifyOtpRequestDto request) {
        return ResponseUtil.success("Verify to reset password successful" ,authenticationService.verifyOtpToResetPassword(request));
    }

    @Operation(
            summary = "Đặt lại mật khẩu",
            description = "Dùng để đặt lại mật khẩu sau khi đã nhập được OTP"
    )
    @PostMapping(UrlConstant.Auth.RESET_PASSWORD)
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        return ResponseUtil.success("Reset password successful", authenticationService.resetPassword(request));
    }
}
