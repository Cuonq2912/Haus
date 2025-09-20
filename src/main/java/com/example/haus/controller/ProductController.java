package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.request.product.ProductRequestDto;
import com.example.haus.domain.dto.request.product.ProductFilterRequestDto;
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
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequestDto request) {
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
            @Valid @RequestBody ProductRequestDto request) {
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
                HttpStatus.OK,
                SuccessMessage.Product.DELETE_PRODUCT_SUCCESS
        );
    }

    @Tag(name = "public-product-controller", description = "Public Product APIs")
    @Operation(
            summary = "Lấy sản phẩm theo tên category",
            description = "Dùng để lấy danh sách sản phẩm thuộc category có phân trang"
    )
    @GetMapping(UrlConstant.Product.GET_PRODUCTS_BY_CATEGORY)
    public ResponseEntity<?> getProductsByCategory(
                    @PathVariable String categoryName,
                    @RequestParam(defaultValue = "1") Integer pageNum,
                    @RequestParam(defaultValue = "10") Integer pageSize) {
            PaginationRequestDto paginationRequest = new PaginationRequestDto(pageNum, pageSize);
            return ResponseUtil.success(
                            SuccessMessage.Product.GET_PRODUCT_SUCCESS,
                            productService.getProductsByCategory(categoryName, paginationRequest));
    }

    @Tag(name = "public-product-controller", description = "Public Product APIs")
    @Operation(
            summary = "Lấy sản phẩm theo ID category",
            description = "Dùng để lấy danh sách sản phẩm thuộc category có phân trang"
    )
    @GetMapping(UrlConstant.Product.GET_PRODUCTS_BY_CATEGORY_ID)
    public ResponseEntity<?> getProductsByCategoryId(
                    @PathVariable Long categoryId,
                    @RequestParam(defaultValue = "1") Integer pageNum,
                    @RequestParam(defaultValue = "10") Integer pageSize) {
            PaginationRequestDto paginationRequest = new PaginationRequestDto(pageNum, pageSize);
            return ResponseUtil.success(
                            SuccessMessage.Product.GET_PRODUCT_SUCCESS,
                            productService.getProductsByCategoryId(categoryId, paginationRequest));
    }

    @Tag(name = "public-product-controller", description = "Public Product APIs")
    @Operation(
            summary = "Tìm kiếm sản phẩm theo từ khóa",
            description = "Tìm kiếm sản phẩm trong tên, mô tả, hoặc mô tả chi tiết có phân trang"
    )
    @GetMapping(UrlConstant.Product.SEARCH_PRODUCTS_BY_KEYWORD)
    public ResponseEntity<?> searchProductsByKeyword(
                    @RequestParam String keyword,
                    @RequestParam(defaultValue = "1") Integer pageNum,
                    @RequestParam(defaultValue = "10") Integer pageSize) {
            PaginationRequestDto paginationRequest = new PaginationRequestDto(pageNum, pageSize);
            return ResponseUtil.success(
                            SuccessMessage.Product.GET_PRODUCT_SUCCESS,
                            productService.searchProductsByKeyword(keyword, paginationRequest));
    }

    @Tag(name = "public-product-controller", description = "Public Product APIs")
    @Operation(
            summary = "Lọc sản phẩm theo nhiều tiêu chí",
            description = "Lọc sản phẩm theo khoảng giá, màu sắc, kiểu dáng với phân trang"
    )
    @PostMapping(UrlConstant.Product.FILTER_PRODUCTS)
    public ResponseEntity<?> filterProducts(
                    @RequestBody @Valid ProductFilterRequestDto filterRequest,
                    @RequestParam(defaultValue = "1") Integer pageNum,
                    @RequestParam(defaultValue = "10") Integer pageSize) {
            PaginationRequestDto paginationRequest = new PaginationRequestDto(pageNum, pageSize);
            return ResponseUtil.success(
                            SuccessMessage.Product.GET_PRODUCT_SUCCESS,
                            productService.filterProducts(filterRequest, paginationRequest));
    }

    @Tag(name = "public-product-controller", description = "Public Product APIs")
    @Operation(
            summary = "Lấy tất cả sản phẩm với phân trang",
            description = "Lấy danh sách tất cả sản phẩm với phân trang và các tùy chọn sắp xếp productName, createdAt, price"
    )
    @GetMapping(UrlConstant.Product.GET_ALL_PRODUCTS)
    public ResponseEntity<?> getAllProducts(
                    @RequestParam(defaultValue = "1") Integer pageNum,
                    @RequestParam(defaultValue = "10") Integer pageSize,
                    @RequestParam(defaultValue = "productName") String sortBy,
                    @RequestParam(defaultValue = "ASC") String sortType) {

            PaginationRequestDto paginationRequest = new PaginationRequestDto(pageNum, pageSize, sortBy, sortType);
            return ResponseUtil.success(
                            SuccessMessage.Product.GET_PRODUCT_SUCCESS,
                            productService.getAllProducts(paginationRequest));
    }

}