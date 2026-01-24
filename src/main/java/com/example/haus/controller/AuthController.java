package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.dto.request.auth.*;
import com.example.haus.domain.dto.request.auth.otp.VerifyOtpRequestDto;
import com.example.haus.domain.dto.response.auth.LoginResponseDto;
import com.example.haus.domain.dto.response.auth.RefreshTokenResponseDto;
import com.example.haus.domain.dto.response.user.UserResponseDto;
import com.example.haus.domain.dto.response.utils.ResponseData;
import com.example.haus.service.AuthenticationService;
import com.google.api.client.util.Value;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@Validated
@RequiredArgsConstructor
@Slf4j(topic = "AUTH-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthController {

    final AuthenticationService authenticationService;

    @Value("${security.cookie.name:refresh_token}")
    String cookieName;

    @Value("${security.cookie.max-age-seconds:604800}")
    int cookieMaxAge;
    
    @Value("${security.cookie.secure:true}")
    boolean cookieSecure;
    
    @Value("${security.cookie.path:/api/v1/auth}")
    String cookiePath;



    @Operation(
            summary = "Đăng nhập tài khoản",
            description = "Dùng để đăng nhập tài khoản"
    )
    @PostMapping(UrlConstant.Auth.LOGIN)
    public ResponseEntity<ResponseData<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {

        SecurityContextHolder.getContext().getAuthentication();

        LoginResponseDto result = authenticationService.authentication(loginRequestDto);

        setRefreshTokenCookie(response, result.getRefreshToken());

        result.setRefreshToken(null);

        return ResponseUtil.success(
                SuccessMessage.Auth.LOGIN_SUCCESS,
                result
        );
    }

    @Operation(
            summary = "Đăng xuất tài khoản",
            description = "Dùng để đăng xuất tài khoản"
    )
    @PostMapping(UrlConstant.Auth.LOGOUT)
    public ResponseEntity<ResponseData<Void>> logout(@CookieValue(name = "refresh_token", required = false) String refreshToken,
                                                    HttpServletResponse response) {
        authenticationService.logout(refreshToken);

        deleteRefreshTokenCookie(response);
        return ResponseUtil.success(HttpStatus.NO_CONTENT, SuccessMessage.Auth.LOGOUT_SUCCESS);
    }

    @Operation(
            summary = "Làm mới token",
            description = "Dùng để cấp lại token"
    )
    @PostMapping(UrlConstant.Auth.REFRESH_TOKEN)
    public ResponseEntity<ResponseData<RefreshTokenResponseDto>> refresh(@CookieValue(name = "refresh_token") String refreshToken,
                                                        HttpServletResponse response) {
        
        RefreshTokenResponseDto result = authenticationService.refresh(refreshToken);

        setRefreshTokenCookie(response, result.getRefreshToken());

        result.setRefreshToken(null);

        return ResponseUtil.success(
                SuccessMessage.Auth.REFRESH_TOKEN_SUCCESS,
                result
        );
    }

    @Operation(
            summary = "Đăng kí tài khoản",
            description = "Dùng để đăng kí tài khoản"
    )
    @PostMapping(UrlConstant.Auth.REGISTER)
    public ResponseEntity<ResponseData<Void>> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        authenticationService.register(registerRequestDto);
        return ResponseUtil.success(HttpStatus.CREATED, SuccessMessage.Auth.REGISTER_SEND_OTP_SUCCESS);
    }

    @Operation(
            summary = "Xác thực OTP",
            description = "Dùng để xác thực OTP sau khi yêu cầu đăng kí tài khoản"
    )
    @PostMapping(UrlConstant.Auth.VERIFY_OTP)
    public ResponseEntity<ResponseData<UserResponseDto>> verify(@Valid @RequestBody VerifyOtpRequestDto verifyOtpRequestDto) {
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
    public ResponseEntity<ResponseData<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto) {
        authenticationService.forgotPassword(forgotPasswordRequestDto);
        return ResponseUtil.success(HttpStatus.ACCEPTED, SuccessMessage.Auth.FORGOT_PASSWORD_SUCCESS);
    }

    @Operation(
            summary = "Xác thực OTP",
            description = "Dùng để xác thực OTP sau khi yêu cầu lấy lại mật khẩu"
    )
    @PostMapping(UrlConstant.Auth.VERIFY_OTP_TO_RESET_PASSWORD)
    public ResponseEntity<ResponseData<Void>> verifyToResetPassword(@Valid @RequestBody VerifyOtpRequestDto request) {
        authenticationService.verifyOtpToResetPassword(request);
        return ResponseUtil.success(
                HttpStatus.OK,
                SuccessMessage.Auth.VERIFY_OTP_TO_RESET_PASSWORD_SUCCESS);
    }

    @Operation(
            summary = "Đặt lại mật khẩu",
            description = "Dùng để đặt lại mật khẩu sau khi đã nhập được OTP"
    )
    @PostMapping(UrlConstant.Auth.RESET_PASSWORD)
    public ResponseEntity<ResponseData<UserResponseDto>> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        return ResponseUtil.success(
                SuccessMessage.Auth.RESET_PASSWORD_SUCCESS,
                authenticationService.resetPassword(request)
        );
    }


    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(cookieName, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(cookieMaxAge);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    private void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }


}
