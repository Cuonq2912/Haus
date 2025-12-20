package com.example.haus.service;

import com.example.haus.domain.dto.request.address.AddressRequestDto;
import com.example.haus.domain.dto.response.address.AddressResponseDto;

import java.util.List;

public interface AddressService {

    AddressResponseDto addAddress(String email, AddressRequestDto addressRequestDto);

    AddressResponseDto updateAddress(String email, Long id, AddressRequestDto addressRequestDto);

    AddressResponseDto getAddressById(Long id);

    List<AddressResponseDto> getAddressesByUserId(String email);

    void deleteAddress(String email, Long id);
}
