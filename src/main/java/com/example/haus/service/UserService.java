package com.example.haus.service;

import com.example.haus.domain.entity.user.User;
import com.example.haus.domain.request.user.profile.ConfirmPasswordRequestDto;
import com.example.haus.domain.response.user.UserResponseDto;
import org.springframework.security.core.Authentication;

public interface UserService {

    User findByUsername(String username);

    void deleteAccount(Authentication authentication);

    UserResponseDto getDetailProfile(Authentication authentication);

    UserResponseDto updateDetailProfile(ConfirmPasswordRequestDto requestDto, Authentication authentication);

}
