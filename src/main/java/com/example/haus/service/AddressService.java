package com.example.haus.service;

import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.address.AddressRequestDto;
import com.example.haus.domain.dto.response.address.AddressResponseDto;

import java.util.List;

public interface AddressService {

    AddressResponseDto addAddress(String userId, AddressRequestDto addressRequestDto);

    AddressResponseDto updateAddress(Long id, AddressRequestDto addressRequestDto);

    AddressResponseDto getAddressById(Long id);

    List<AddressResponseDto> getAddressesByUserId(String userId);

    void deleteAddress(Long id);
}
