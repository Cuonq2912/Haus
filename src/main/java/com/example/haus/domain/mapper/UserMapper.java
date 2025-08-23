package com.example.haus.domain.mapper;

import com.example.haus.domain.entity.user.User;
import com.example.haus.domain.request.admin.CreateUserRequestDto;
import com.example.haus.domain.request.admin.UpdateUserRequestDto;
import com.example.haus.domain.request.user.profile.UpdatePersonalInformationRequestDto;
import com.example.haus.domain.response.user.UserResponseDto;
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

    User createUserRequestDtoToUser(CreateUserRequestDto request);

    void updateUserFromDto(UpdateUserRequestDto request, @MappingTarget User user);

    void updateUserFromPersonalInformationDto(UpdatePersonalInformationRequestDto request, @MappingTarget User user);
}
