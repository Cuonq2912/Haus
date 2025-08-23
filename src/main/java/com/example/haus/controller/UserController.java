package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.request.user.profile.ConfirmPasswordRequestDto;
import com.example.haus.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
@RequestMapping("/user")
@Validated
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Xóa tài khoản",
            description = "Dùng để người dùng xóa tài khoản của mình (soft delete)",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @DeleteMapping(UrlConstant.User.DELETE_MY_ACCOUNT)
    public ResponseEntity<?> deleteMyAccount(Authentication authentication) {
        userService.deleteAccount(authentication);
        return ResponseUtil.success("Xóa tài khoản thành công");
    }

    @Operation(
            summary = "Lấy thông tin profile (thông tin user và user health)",
            description = "Dùng để người dùng lấy thông tin profile đầy đủ (thông tin cá nhân + sức khỏe)",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @GetMapping(UrlConstant.User.GET_PROFILE)
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        return ResponseUtil.success(userService.getDetailProfile(authentication));
    }

    @Operation(
            summary = "Cập nhật thông tin profile",
            description = "Dùng để người dùng cập nhật thông tin cá nhân và sức khỏe với xác nhận mật khẩu",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PutMapping(UrlConstant.User.UPDATE_PROFILE)
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody ConfirmPasswordRequestDto request,
            Authentication authentication
    ) {
        return ResponseUtil.success(userService.updateDetailProfile(request, authentication));
    }
}
