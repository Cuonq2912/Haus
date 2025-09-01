package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.request.product.CreateProductRequestDto;
import com.example.haus.domain.request.product.UpdateProductRequestDto;
import com.example.haus.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class ProductController {

    ProductService productService;

    @Tag(name = "admin-product-controller", description = "Admin Product Management APIs")
    @Operation(
            summary = "Lấy sản phẩm theo id",
            description = "Dùng để lấy ra sản phẩm theo id",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @GetMapping(UrlConstant.Product.GET_PRODUCT_BY_ID)
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return ResponseUtil.success(
                SuccessMessage.Product.GET_PRODUCT_SUCCESS,
                productService.getProductById(id));
    }

    @Tag(name = "admin-product-controller", description = "Admin Product Management APIs")
    @Operation(
            summary = "Tạo sản phẩm mới",
            description = "Dùng để tạo sản phẩm mới",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PostMapping(UrlConstant.Product.CREATE_PRODUCT)
    public ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductRequestDto request) {
        return ResponseUtil.success(
                HttpStatus.CREATED,
                SuccessMessage.Product.CREATE_PRODUCT_SUCCESS,
                productService.createProduct(request));
    }

    @Tag(name = "admin-product-controller", description = "Admin Product Management APIs")
    @Operation(
            summary = "Cập nhật sản phẩm",
            description = "Dùng để cập nhật thông tin sản phẩm theo id",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PutMapping(UrlConstant.Product.UPDATE_PRODUCT)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequestDto request) {
        return ResponseUtil.success(
                SuccessMessage.Product.UPDATE_PRODUCT_SUCCESS,
                productService.updateProduct(id, request));
    }

    @Tag(name = "admin-product-controller", description = "Admin Product Management APIs")
    @Operation(
            summary = "Xóa sản phẩm",
            description = "Dùng để xóa sản phẩm theo id",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @DeleteMapping(UrlConstant.Product.DELETE_PRODUCT)
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseUtil.success(
                HttpStatus.NO_CONTENT,
                SuccessMessage.Product.DELETE_PRODUCT_SUCCESS
        );
    }

}