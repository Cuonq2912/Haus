package com.example.haus.domain.mapper;

import com.example.haus.domain.entity.user.User;
import com.example.haus.domain.request.auth.RegisterRequestDto;
import com.example.haus.domain.response.user.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface AuthMapper {

    @Mapping(target = "password", ignore = true)
    User registerRequestDtoToUser(RegisterRequestDto request);

    UserResponseDto userToUserResponseDto(User user);
}
