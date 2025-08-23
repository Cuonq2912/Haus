package com.example.haus.domain.mapper;

import com.example.haus.domain.entity.address.Address;
import com.example.haus.domain.entity.user.User;
import com.example.haus.domain.request.admin.CreateUserRequestDto;
import com.example.haus.domain.request.admin.UpdateUserRequestDto;
import com.example.haus.domain.request.user.profile.UpdateAddressRequestDto;
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
public interface AddressMapper {
    UpdateAddressRequestDto addressUpdateAddressRequestDto(Address address);

    Address updateAddressRequestDtoToAddress(UpdateAddressRequestDto UpdateAddressRequestDto);

}
