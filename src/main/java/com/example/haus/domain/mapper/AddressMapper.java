package com.example.haus.domain.mapper;

import com.example.haus.domain.entity.address.Address;
import com.example.haus.domain.dto.request.user.profile.UpdateAddressRequestDto;
import org.mapstruct.Mapper;
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
