package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.request.user.profile.ConfirmPasswordUpdateUserRequestDto;
import com.example.haus.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @Operation(
            summary = "Xóa tài khoản",
            description = "Dùng để người dùng xóa tài khoản của mình (soft delete)",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @DeleteMapping(UrlConstant.User.DELETE_MY_ACCOUNT)
    public ResponseEntity<?> deleteMyAccount(Authentication authentication) {
        userService.deleteAccount(authentication);
          return ResponseUtil.success(
                  HttpStatus.OK,
                  SuccessMessage.User.SOFT_DELETE_SUCCESS
          );
    }

    @Operation(
            summary = "Lấy thông tin profile",
            description = "Dùng để người dùng lấy thông tin profile",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @GetMapping(UrlConstant.User.GET_PROFILE)
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        return ResponseUtil.success(
                SuccessMessage.User.GET_MY_PROFILE_SUCCESS,
                userService.getDetailProfile(authentication)
        );
    }

    @Operation(
            summary = "Cập nhật thông tin cá nhân",
            description = "Dùng để người dùng cập nhật thông tin cá nhân",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PutMapping(UrlConstant.User.UPDATE_PROFILE)
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody ConfirmPasswordUpdateUserRequestDto request,
            Authentication authentication
    ) {
        return ResponseUtil.success(
                SuccessMessage.User.UPDATE_PROFILE_SUCCESS,
                userService.updateDetailProfile(request, authentication)
        );
    }
}
