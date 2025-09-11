package com.example.haus.service.impl;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.mapper.ProductMapper;
import com.example.haus.domain.request.product.CreateProductRequestDto;
import com.example.haus.domain.request.product.UpdateProductRequestDto;
import com.example.haus.domain.response.product.ProductResponseDto;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.ProductRepository;
import com.example.haus.service.ProductService;
import com.example.haus.util.UploadFileUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;

    UploadFileUtil uploadFileUtil;

    ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED));
        return productMapper.toProductResponseDto(product);
    }

    @Override
    public ProductResponseDto createProduct(CreateProductRequestDto request) {

        if (productRepository.existsProductByProductName(request.getProductName())) {
            throw new InvalidDataException(ErrorMessage.Product.ERR_PRODUCT_NAME_EXISTED);
        }

        Product product = productMapper.createProductRequestDtoToProduct(request);

        Date now = new Date();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        if (product.getInventoryQuantity() == null) {
            product.setInventoryQuantity(0);
        }

        Product savedProduct = productRepository.save(product);

        return productMapper.toProductResponseDto(savedProduct);
    }

    @Override
    public ProductResponseDto updateProduct(Long productId, UpdateProductRequestDto request) {
        if (productId == null || productId <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED));

        if (!product.getProductName().equals(request.getProductName()) &&
                productRepository.existsProductByProductName(request.getProductName())) {
            throw new InvalidDataException(ErrorMessage.Product.ERR_PRODUCT_NAME_EXISTED);
        }

        productMapper.updateProductFromDto(request, product);

        product.setUpdatedAt(new Date());

        Product updatedProduct = productRepository.save(product);

        return productMapper.toProductResponseDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        if (productId == null || productId <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED));

        if (product.getMedias() != null) {
            product.getMedias().forEach(media -> {
                if (StringUtils.isNotBlank(media.getUrl())) {
                    uploadFileUtil.destroyFileWithUrl(media.getUrl());
                }
            });
        }

        if (product.getProductVariations() != null) {
            product.getProductVariations().forEach(variation -> {
                if (variation.getMedia() != null && StringUtils.isNotBlank(variation.getMedia().getUrl())) {
                    uploadFileUtil.destroyFileWithUrl(variation.getMedia().getUrl());
                }
            });
        }

        productRepository.deleteById(productId);
    }

}
