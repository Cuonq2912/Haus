package com.example.haus.domain.mapper;

import com.example.haus.domain.entity.user.User;
import com.example.haus.domain.dto.request.admin.CreateUserRequestDto;
import com.example.haus.domain.dto.request.user.profile.UpdateUserRequestDto;
import com.example.haus.domain.dto.response.user.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface UserMapper {
    UserResponseDto userToUserResponseDto(User user);

    void updateUserFromPersonalInformationDto(UpdateUserRequestDto request, @MappingTarget User user);
}
