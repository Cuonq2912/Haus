package com.example.haus.service.impl;

import com.example.haus.constant.CommonConstant;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.entity.user.User;
import com.example.haus.domain.mapper.UserMapper;
import com.example.haus.domain.request.user.profile.ConfirmPasswordUpdateUserRequestDto;
import com.example.haus.domain.request.user.profile.UpdateUserRequestDto;
import com.example.haus.domain.response.user.UserResponseDto;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.helper.PersonalInformationHelper;
import com.example.haus.repository.UserRepository;
import com.example.haus.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    UserMapper userMapper;

    PersonalInformationHelper personalInformationHelper;

    @Override
    public void deleteAccount(Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new InvalidDataException(ErrorMessage.User.ERR_ACCOUNT_ALREADY_DELETED);
        }

        user.setIsDeleted(CommonConstant.TRUE);
        user.setDeletedAt(new Date());

        userRepository.save(user);
    }

    @Override
    public UserResponseDto getDetailProfile(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        UserResponseDto userResponseDto = userMapper.userToUserResponseDto(user);

        return userResponseDto;
    }

    @Override
    public UserResponseDto updateDetailProfile(ConfirmPasswordUpdateUserRequestDto requestDto, Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        if (requestDto.getProfileData() != null) {

            UpdateUserRequestDto personalInfo = personalInformationHelper
                    .handleEmptyStrings(requestDto.getProfileData());

            userMapper.updateUserFromPersonalInformationDto(personalInfo, user);

        }

        User updatedUser = userRepository.save(user);

        UserResponseDto userResponseDto = userMapper.userToUserResponseDto(updatedUser);

        return userResponseDto;
    }
}
