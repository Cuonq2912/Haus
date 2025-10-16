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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            description = "Dùng để khách hàng thêm địa chỉ khi cập nhật thông tin hoặc địa chỉ nhận hàng",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PostMapping(UrlConstant.Address.ADD_ADDRESS)
    public ResponseEntity<?> addAddress (@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody AddressRequestDto addressRequestDto){
        return ResponseUtil.success(
                SuccessMessage.Address.ADD_ADDRESS_SUCCESS,
                addressService.addAddress(userDetails.getUsername(), addressRequestDto)
        );
    }

    @Operation(
            summary = "Lấy chi tiet dia chi theo ID",
            description = "Dùng để khach hang lấy dia chi theo ID",
            security = @SecurityRequirement(name = "Bearer Token")
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
            description = "Dùng để khach hang lấy danh sách địa chỉ theo user ID",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @GetMapping(UrlConstant.Address.GET_ADDRESSES_BY_USER_ID)
    public ResponseEntity<?> getAddressesByUserId(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseUtil.success(
                SuccessMessage.Address.GET_ADDRESS_SUCCESS,
                addressService.getAddressesByUserId(userDetails.getUsername())
        );
    }

    @Operation(
            summary = "Cập nhật địa chỉ",
            description = "Dùng để khách hàng cập nhật địa chỉ theo ID",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PutMapping(UrlConstant.Address.UPDATE_ADDRESS)
    public ResponseEntity<?> updateCategory(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("id") Long id, @Valid @RequestBody AddressRequestDto addressRequestDto){
        return ResponseUtil.success(
                SuccessMessage.Address.UPDATE_ADDRESS_SUCCESS,
                addressService.updateAddress(userDetails.getUsername(), id, addressRequestDto)
        );
    }

    @Operation(
            summary = "Xóa địa chỉ theo Id",
            description = "Dùng để khách hàng xóa địa chỉ theo ID",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @DeleteMapping(UrlConstant.Address.DELETE_ADDRESS)
    public ResponseEntity<?> deleteCategory(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id){
        addressService.deleteAddress(userDetails.getUsername(), id);
        return ResponseUtil.success(
                HttpStatus.NO_CONTENT,
                SuccessMessage.Address.DELETE_ADDRESS_SUCCESS
        );
    }
}
