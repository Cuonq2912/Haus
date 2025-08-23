package com.example.haus.service.impl;

import com.example.haus.constant.CommonConstant;
import com.example.haus.domain.entity.address.Address;
import com.example.haus.domain.entity.user.User;
import com.example.haus.domain.mapper.AddressMapper;
import com.example.haus.domain.mapper.UserMapper;
import com.example.haus.domain.request.user.profile.ConfirmPasswordRequestDto;
import com.example.haus.domain.request.user.profile.UpdatePersonalInformationRequestDto;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    UserMapper userMapper;

    PersonalInformationHelper personalInformationHelper;

    AddressMapper addressMapper;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->  new UsernameNotFoundException("Username not found"));
    }

    @Override
    @Transactional()
    public void deleteAccount(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Username not found"));

        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new InvalidDataException("Usernaem đã bị xóa");
        }

        user.setIsDeleted(CommonConstant.TRUE);
        user.setDeletedAt(new Date());

        userRepository.save(user);
    }

    @Override
    public UserResponseDto getDetailProfile(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Username not found"));

        UserResponseDto userResponseDto = userMapper.userToUserResponseDto(user);

        return userResponseDto;
    }

    @Override
    public UserResponseDto updateDetailProfile(ConfirmPasswordRequestDto requestDto, Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Username not found"));


        if (requestDto.getProfileData() != null) {

            UpdatePersonalInformationRequestDto personalInfo = personalInformationHelper.handleEmptyStrings(requestDto.getProfileData());

            userMapper.updateUserFromPersonalInformationDto(personalInfo, user);

            if (personalInfo.getUpdateAddressRequestDto() != null) {
                Address address = addressMapper.updateAddressRequestDtoToAddress(requestDto.getProfileData().getUpdateAddressRequestDto());

                user.setAddress(address);
            }
        }

        User updatedUser = userRepository.save(user);

        UserResponseDto userResponseDto = userMapper.userToUserResponseDto(updatedUser);

        return userResponseDto;
    }
}
