package com.example.haus.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.haus.constant.CommonConstant;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.MediaType;
import com.example.haus.domain.entity.user.User;
import com.example.haus.domain.mapper.UserMapper;
import com.example.haus.domain.dto.request.user.profile.ConfirmPasswordUpdateUserRequestDto;
import com.example.haus.domain.dto.request.user.profile.UpdatePasswordRequestDto;
import com.example.haus.domain.dto.request.user.profile.UpdateUserRequestDto;
import com.example.haus.domain.dto.response.user.UserResponseDto;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.KeycloakException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.exception.UploadFileException;
import com.example.haus.helper.PersonalInformationHelper;
import com.example.haus.repository.UserRepository;
import com.example.haus.service.FileValidatorService;
import com.example.haus.service.UserService;
import com.example.haus.util.keycloak.KeycloakUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    PersonalInformationHelper personalInformationHelper;
    Cloudinary cloudinary;
    KeycloakUtil keycloakUtil;
    FileValidatorService fileValidatorService;

    @Override
    public void deleteAccount(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsernameAndIsDeletedFalse(username).orElseThrow(
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

        User user = userRepository.findByUsernameAndIsDeletedFalse(username).orElseThrow(
                () -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        return userMapper.userToUserResponseDto(user);
    }

    @Override
    public UserResponseDto updateDetailProfile(ConfirmPasswordUpdateUserRequestDto requestDto, Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsernameAndIsDeletedFalse(username).orElseThrow(
                () -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        if (requestDto.getProfileData() != null) {
            UpdateUserRequestDto personalInfo = personalInformationHelper
                    .handleEmptyStrings(requestDto.getProfileData());

            userMapper.updateUserFromPersonalInformationDto(personalInfo, user);
        }

        User updatedUser = userRepository.save(user);

        return userMapper.userToUserResponseDto(updatedUser);
    }

    @Override
    public void updatePassword(UpdatePasswordRequestDto request, Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        PasswordEncoder encoder = new BCryptPasswordEncoder(10);

        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidDataException(ErrorMessage.User.ERR_INCORRECT_PASSWORD);
        }

        if (encoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new InvalidDataException(ErrorMessage.User.ERR_DUPLICATE_OLD_PASSWORD);
        }

        String userId = keycloakUtil.getUserId(username);

        boolean isUpdatedInKC = keycloakUtil.resetPassword(userId, request.getNewPassword());
        if (!isUpdatedInKC) {
            throw new KeycloakException(ErrorMessage.Auth.ERR_RESET_PASSWORD_FAILED_IN_KEYCLOAK);
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password updated successfully for user {}", username);
    }

    @Override
    public UserResponseDto uploadAvatar(MultipartFile file, Authentication authentication) throws IOException {
        fileValidatorService.validateFile(file, MediaType.IMAGE);

        User user = userRepository.findByUsernameAndIsDeletedFalse(authentication.getName()).orElseThrow(
                () -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        if(user.getAvatarPublicId() != null){
            cloudinary.uploader().destroy(user.getAvatarPublicId(), ObjectUtils.emptyMap());
        }

        String imageUrl;
        String publicId;
        try{
            String safeFilename = fileValidatorService.generateSafeFileName(file.getOriginalFilename());

            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", "haus/avatars",
                    "public_id", safeFilename,
                    "resource_type", "image",
                    "overwrite", true,
                    "transformation", "w_400,h_400,c_fill,q_auto");

            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            publicId = (String) result.get("public_id");
            imageUrl = (String) result.get("secure_url");
        } catch (IOException e) {
            throw new UploadFileException(ErrorMessage.User.UPLOAD_AVATAR_FAIL, e);
        }
        user.setAvatarLink(imageUrl);
        user.setAvatarPublicId(publicId);

        userRepository.save(user);
        return userMapper.userToUserResponseDto(user);
    }
}
