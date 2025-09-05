package com.example.haus.service;

import com.example.haus.domain.request.user.profile.ConfirmPasswordUpdateUserRequestDto;
import com.example.haus.domain.request.user.profile.UpdatePasswordRequestDto;
import com.example.haus.domain.response.user.UserResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    void deleteAccount(Authentication authentication);

    UserResponseDto getDetailProfile(Authentication authentication);

    UserResponseDto updateDetailProfile(ConfirmPasswordUpdateUserRequestDto requestDto, Authentication authentication);

    void updatePassword(UpdatePasswordRequestDto updatePasswordRequestDto, Authentication authentication);

    UserResponseDto uploadAvatar(MultipartFile file, Authentication authentication) throws IOException;
}
