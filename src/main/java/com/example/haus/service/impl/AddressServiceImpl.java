package com.example.haus.service.impl;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.dto.request.address.AddressRequestDto;
import com.example.haus.domain.dto.response.address.AddressResponseDto;
import com.example.haus.domain.entity.address.Address;
import com.example.haus.domain.entity.user.User;
import com.example.haus.domain.mapper.AddressMapper;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.AddressRepository;
import com.example.haus.repository.UserRepository;
import com.example.haus.service.AddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressServiceImpl implements AddressService {

    AddressMapper addressMapper;

    AddressRepository addressRepository;

    UserRepository userRepository;


    @Override
    public AddressResponseDto addAddress(String email, AddressRequestDto addressRequestDto) {
        User user = userRepository.findByUsernameAndIsDeletedFalse(email).orElseThrow(() ->
                new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        Address address = addressMapper.addressRequestDtoToAddress(addressRequestDto);

        user.getAddresses().add(address);
        address.setUser(user);

        return addressMapper.addressToAddressResponseDto(addressRepository.save(address));
    }

    @Override
    public AddressResponseDto updateAddress(String email, Long id, AddressRequestDto addressRequestDto) {
        Address address = addressRepository
                .findByIdAndUserUsernameAndIsDeletedFalse(id, email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Address.ERR_ADDRESS_NOT_FOUND));

        addressMapper.updateAddressFromDto(addressRequestDto, address);
        return addressMapper.addressToAddressResponseDto(addressRepository.save(address));
    }

    @Override
    public AddressResponseDto getAddressById(Long id) {
        Address address = addressRepository.findByIdAndIsDeletedFalse(id).orElseThrow(
                () -> new ResourceNotFoundException(ErrorMessage.Address.ERR_ADDRESS_NOT_FOUND)
        );
        return addressMapper.addressToAddressResponseDto(address);
    }

    @Override
    public List<AddressResponseDto> getAddressesByUserId(String email) {
        User user = userRepository.findByUsernameAndIsDeletedFalse(email).orElseThrow(() ->
                new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        List<Address> addresses = addressRepository.getAddressByUserId(user.getId());

        return addresses.stream().map(address -> addressMapper.addressToAddressResponseDto(address)).toList();
    }

    @Override
    public void deleteAddress(String email, Long id) {
        Address address = addressRepository
                .findByIdAndUserUsernameAndIsDeletedFalse(id, email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Address.ERR_ADDRESS_NOT_FOUND));

        address.setIsDeleted(true);
        addressRepository.save(address);
    }
}
