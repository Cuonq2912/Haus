package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.request.address.AddressRequestDto;
import com.example.haus.domain.dto.request.category.CategoryRequestDto;
import com.example.haus.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@Validated
@RequiredArgsConstructor
@Slf4j(topic = "ADDRESS-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressController {

    AddressService addressService;

    @Operation(
            summary = "Thêm địa chỉ",
            description = "Dùng để khách hàng thêm địa chỉ khi cập nhật thông tin hoặc địa chỉ nhận hàng"
    )
    @PostMapping(UrlConstant.Address.ADD_ADDRESS)
    public ResponseEntity<?> addAddress (@PathVariable String userId, @Valid @RequestBody AddressRequestDto addressRequestDto){
        return ResponseUtil.success(
                SuccessMessage.Address.ADD_ADDRESS_SUCCESS,
                addressService.addAddress(userId, addressRequestDto)
        );
    }

    @Operation(
            summary = "Lấy chi tiet dia chi theo ID",
            description = "Dùng để khach hang lấy dia chi theo ID"
    )
    @GetMapping(UrlConstant.Address.GET_ADDRESS)
    public ResponseEntity<?> getAddressById(@PathVariable Long id){
        return ResponseUtil.success(
                SuccessMessage.Address.GET_ADDRESS_SUCCESS,
                addressService.getAddressById(id)
        );
    }

    @Operation(
            summary = "Lấy danh sách địa chỉ theo user ID",
            description = "Dùng để khach hang lấy danh sách địa chỉ theo user ID"
    )
    @GetMapping(UrlConstant.Address.GET_ADDRESSES_BY_USER_ID)
    public ResponseEntity<?> getAddressesByUserId(@PathVariable String userId){
        return ResponseUtil.success(
                SuccessMessage.Address.GET_ADDRESS_SUCCESS,
                addressService.getAddressesByUserId(userId)
        );
    }

    @Operation(
            summary = "Cập nhật địa chỉ",
            description = "Dùng để khách hàng cập nhật địa chỉ theo ID"
    )
    @PutMapping(UrlConstant.Address.UPDATE_ADDRESS)
    public ResponseEntity<?> updateCategory(@PathVariable("id") Long id, @Valid @RequestBody AddressRequestDto addressRequestDto){
        return ResponseUtil.success(
                SuccessMessage.Address.UPDATE_ADDRESS_SUCCESS,
                addressService.updateAddress(id, addressRequestDto)
        );
    }

    @Operation(
            summary = "Xóa địa chỉ theo Id",
            description = "Dùng để khách hàng xóa địa chỉ theo ID"
    )
    @DeleteMapping(UrlConstant.Address.DELETE_ADDRESS)
    public ResponseEntity<?> deleteCategory(@PathVariable Long id){
        addressService.deleteAddress(id);
        return ResponseUtil.success(
                HttpStatus.NO_CONTENT,
                SuccessMessage.Address.DELETE_ADDRESS_SUCCESS
        );
    }
}
