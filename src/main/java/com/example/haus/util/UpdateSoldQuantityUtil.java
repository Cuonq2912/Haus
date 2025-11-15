package com.example.haus.util;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.entity.product.ProductVariation;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.ProductRepository;
import com.example.haus.repository.ProductVariationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UpdateSoldQuantityUtil {

    ProductVariationRepository productVariationRepository;

    ProductRepository productRepository;

    public void updateProductTotalInventoryAndSoldQuantity(Long productId) {
        List<ProductVariation> activeVariations = productVariationRepository
                .findByProductId(productId);

        int totalQuantity = activeVariations.stream()
                .mapToInt(ProductVariation::getInventoryQuantity)
                .sum();

        int soldQuantity = activeVariations.stream()
                .collect(Collectors.summingInt(ProductVariation::getSoldQuantity));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED));

        product.setInventoryQuantity(totalQuantity);
        product.setSoldQuantity(soldQuantity);
        product.setUpdatedAt(new Date());
        productRepository.save(product);
    }
}
