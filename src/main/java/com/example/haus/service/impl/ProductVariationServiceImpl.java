package com.example.haus.service.impl;

import com.example.haus.constant.CommonConstant;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.MediaType;
import com.example.haus.domain.entity.product.Media;
import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.entity.product.ProductVariation;
import com.example.haus.domain.mapper.ProductVariationMapper;
import com.example.haus.domain.dto.request.product.CreateProductVariationRequestDto;
import com.example.haus.domain.dto.request.product.UpdateProductVariationRequestDto;
import com.example.haus.domain.dto.response.product.ProductVariationResponseDto;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.ProductRepository;
import com.example.haus.repository.ProductVariationRepository;
import com.example.haus.service.ProductVariationService;
import com.example.haus.util.UploadFileUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProductVariationServiceImpl implements ProductVariationService {

    ProductVariationRepository productVariationRepository;
    ProductRepository productRepository;
    UploadFileUtil uploadFileUtil;

    ProductVariationMapper productVariationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariationResponseDto> getProductVariationsByProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED));

        if (CommonConstant.TRUE.equals(product.getIsDeleted()))
            throw new InvalidDataException(ErrorMessage.Product.ERR_PRODUCT_ALREADY_DELETED);

        List<ProductVariation> productVariations = productVariationRepository
                .findByProductIdAndIsDeletedFalse(productId);

        return productVariationMapper.toListProductVariationResponseDto(productVariations);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariationResponseDto getProductVariationById(Long productVariationId) {
        if (productVariationId == null || productVariationId <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        ProductVariation productVariation = productVariationRepository.findById(productVariationId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_VARIATION_NOT_EXISTED));

        if (CommonConstant.TRUE.equals(productVariation.getIsDeleted()))
            throw new InvalidDataException(ErrorMessage.Product.ERR_PRODUCT_VARIATION_ALREADY_DELETED);

        return productVariationMapper.toProductVariationResponseDto(productVariation);
    }

    @Override
    @Transactional
    public ProductVariationResponseDto createProductVariation(CreateProductVariationRequestDto request) {
        if (request == null) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED));

        ProductVariation productVariation = productVariationMapper.toProductVariation(request);
        productVariation.setProduct(product);

        Date now = new Date();
        productVariation.setCreatedAt(now);

        productVariation.setIsDeleted(CommonConstant.FALSE);

        product.setInventoryQuantity(product.getInventoryQuantity() + request.getInventoryQuantity());

        String imageUrl = null;
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            imageUrl = uploadFileUtil.uploadFile(request.getImageFile());
        }

        if (StringUtils.isNotBlank(imageUrl)) {
            Media media = Media.builder()
                    .url(imageUrl)
                    .type(MediaType.Image)
                    .productVariation(productVariation)
                    .build();
            productVariation.setMedia(media);
        }

        ProductVariation savedVariation = productVariationRepository.save(productVariation);

        return productVariationMapper.toProductVariationResponseDto(savedVariation);
    }

    @Override
    public ProductVariationResponseDto updateProductVariation(UpdateProductVariationRequestDto request) {
        if (request == null || request.getId() == null || request.getId() <= 0) {
            throw new InvalidDataException(ErrorMessage.Product.ERR_PRODUCT_VARIATION_NOT_EXISTED);
        }

        ProductVariation existingVariation = productVariationRepository.findById(request.getId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_VARIATION_NOT_EXISTED));

        if (CommonConstant.TRUE.equals(existingVariation.getIsDeleted()))
            throw new InvalidDataException(ErrorMessage.Product.ERR_PRODUCT_VARIATION_ALREADY_DELETED);

        Product product = existingVariation.getProduct();
        Integer oldVariationQuantity = existingVariation.getInventoryQuantity();

        productVariationMapper.updateProductVariationFromDto(existingVariation, request);

        if (request.getInventoryQuantity() != null && !request.getInventoryQuantity().equals(oldVariationQuantity)) {
            int totalQuantity = calculateTotalInventoryQuantity(product.getId(), null, request.getInventoryQuantity(),
                    existingVariation.getId());
            product.setInventoryQuantity(totalQuantity);
            productRepository.save(product);
        }

        String imageUrl = null;
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            imageUrl = uploadFileUtil.uploadFile(request.getImageFile());
        }

        if (StringUtils.isNotBlank(imageUrl)) {
            if (existingVariation.getMedia() != null) {
                existingVariation.getMedia().setUrl(imageUrl);
            } else {
                Media media = Media.builder()
                        .url(imageUrl)
                        .type(MediaType.Image)
                        .productVariation(existingVariation)
                        .build();
                existingVariation.setMedia(media);
            }
        }

        existingVariation.setUpdatedAt(new Date());

        ProductVariation updatedVariation = productVariationRepository.save(existingVariation);

        return productVariationMapper.toProductVariationResponseDto(updatedVariation);
    }

    @Override
    public void deleteProductVariation(Long productVariationId) {
        if (productVariationId == null || productVariationId <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        ProductVariation productVariation = productVariationRepository.findById(productVariationId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_VARIATION_NOT_EXISTED));

        if (productVariation.getMedia() != null && StringUtils.isNotBlank(productVariation.getMedia().getUrl())) {
            uploadFileUtil.destroyFileWithUrl(productVariation.getMedia().getUrl());
        }

        Product product = productVariation.getProduct();
        int totalQuantity = calculateTotalInventoryQuantity(product.getId(), productVariationId, null, null);
        product.setInventoryQuantity(totalQuantity);
        productRepository.save(product);

        productVariation.setIsDeleted(CommonConstant.TRUE);

        productVariationRepository.save(productVariation);
    }

    private int calculateTotalInventoryQuantity(Long productId, Long excludeVariationId, Integer overrideQuantity,
            Long overrideVariationId) {
        List<ProductVariation> allVariations = productVariationRepository
                .findByProductIdAndIsDeletedFalse(productId);
        int totalQuantity = 0;

        for (ProductVariation variation : allVariations) {
            if (excludeVariationId != null && variation.getId().equals(excludeVariationId)) {
                continue;
            }

            if (overrideVariationId != null && variation.getId().equals(overrideVariationId)
                    && overrideQuantity != null) {
                totalQuantity += overrideQuantity;
            } else {
                totalQuantity += variation.getInventoryQuantity();
            }
        }

        return totalQuantity;
    }
}
