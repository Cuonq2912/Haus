package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.request.address.AddressRequestDto;
import com.example.haus.domain.dto.response.address.AddressResponseDto;
import com.example.haus.domain.entity.address.Address;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AddressMapper {
    @Mapping(target = "userId", source = "user.id")
    AddressResponseDto addressToAddressResponseDto(Address address);

    void updateAddressFromDto(AddressRequestDto addressRequestDto, @MappingTarget Address address);

    Address addressRequestDtoToAddress(AddressRequestDto addressRequestDto);
}
