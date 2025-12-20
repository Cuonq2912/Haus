package com.example.haus.util;

import com.example.haus.domain.entity.product.Category;
import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.entity.product.ProductVariation;
import com.example.haus.repository.CategoryRepository;
import com.example.haus.repository.ProductRepository;
import com.example.haus.repository.ProductVariationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppDataSeeder implements ApplicationRunner {

    CategoryRepository categoryRepository;

    ProductRepository productRepository;

    ProductVariationRepository productVariationRepository;

    ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        seedCategory();
        seedProduct();
        seedProductVariation();
    }

    void seedCategory() {
        try (InputStream is = getClass().getResourceAsStream("/data/Category.json")) {
            log.info("Start seeding category from JSON...");

            List<Category> categoriesFromDB = categoryRepository.findAll();

            List<Category> categoriesFromJson = objectMapper.readValue(is, new TypeReference<>() {
            });

            if (categoriesFromDB.isEmpty()) {
                categoryRepository.saveAll(categoriesFromJson);
            } else {
                if (categoriesFromJson.size() > categoriesFromDB.size()) {
                    for (Category x : categoriesFromJson) {
                        if (!categoryRepository.existsByCategoryName(x.getCategoryName())) {
                            categoryRepository.save(x);
                        }
                    }
                }
            }

            log.info("Seeding category from JSON completed!");

        } catch (IOException e) {
            log.warn("Seeding category from JSON fail");
        }
    }

    void seedProduct() {
        try (InputStream is = getClass().getResourceAsStream("/data/Product.json")) {
            log.info("Start seeding product from JSON...");

            List<Product> productsFromDB = productRepository.findAll();
            List<ProductJsonDto> productDtosFromJson = objectMapper.readValue(is, new TypeReference<>() {});

            for (ProductJsonDto dto : productDtosFromJson) {
                // Chỉ chèn nếu DB rỗng HOẶC sản phẩm chưa tồn tại
                if (productsFromDB.isEmpty() || !productRepository.existsByProductCode(dto.productCode)) {
                    Product product = convertToProduct(dto);
                    if (product != null) {
                        if (dto.categories != null) {
                            for (String categoryName : dto.categories) {
                                Optional<Category> categoryOpt = categoryRepository.findByCategoryNameIgnoreCase(categoryName);

                                if (categoryOpt.isPresent()) {
                                    product.addCategory(categoryOpt.get());
                                } else {
                                    log.warn("Category '{}' not found for Product: {}", categoryName, dto.productCode);
                                }
                            }
                        }
                        // -----------------------------------------------------------

                        productRepository.save(product);
                    }
                }
            }

            log.info("Seeding product from JSON completed!");

        } catch (IOException e) {
            log.warn("Seeding product from JSON fail: " + e.getMessage());
        }
    }

    private Product convertToProduct(ProductJsonDto dto) {
        try {
            return Product.builder()
                    .productCode(dto.productCode)
                    .productName(dto.productName)
                    .price(dto.price)
                    .description(dto.description)
                    .detailDescription(dto.detailDescription)
                    .inventoryQuantity(dto.inventoryQuantity)
                    .material(dto.material)
                    .soldQuantity(dto.soldQuantity)
                    .isDeleted(false)
                    .build();
        } catch (Exception e) {
            log.warn("Failed to convert ProductJsonDto to Product for productCode: " + dto.productCode);
            return null;
        }
    }

    static class ProductJsonDto {
        public String productCode;
        public String productName;
        public Double price;
        public String description;
        public String detailDescription;
        public Integer soldQuantity;
        public Integer inventoryQuantity;
        public String material;
        public List<String> categories;
    }

    void seedProductVariation() {
        try (InputStream is = getClass().getResourceAsStream("/data/ProductVariation.json")) {
            log.info("Start seeding product variation from JSON...");

            List<ProductVariation> variationsFromDB = productVariationRepository.findAll();

            List<ProductVariationJsonDto> variationDtosFromJson =
                    objectMapper.readValue(is, new TypeReference<>() {});

            if (variationsFromDB.isEmpty()) {
                for (ProductVariationJsonDto dto : variationDtosFromJson) {
                    ProductVariation variation = convertToProductVariation(dto);
                    if (variation != null) {
                        productVariationRepository.save(variation);
                    }
                }
            } else {
                for (ProductVariationJsonDto dto : variationDtosFromJson) {
                    boolean exists = variationsFromDB
                            .stream()
                            .anyMatch(
                                    v ->
                                            v.getProduct().getId().equals(dto.productId) &&
                                                    v.getColor().equals(dto.color) &&
                                                    v.getSize().equals(dto.size)
                            );


                    if (!exists) {
                        ProductVariation variation = convertToProductVariation(dto);
                        if (variation != null) {
                            productVariationRepository.save(variation);
                        }
                    }
                }
            }

            log.info("Seeding product variation from JSON completed!");
        } catch (IOException e) {
            log.warn("Seeding product variation from JSON fail: " + e.getMessage());
        }
    }

    private ProductVariation convertToProductVariation(ProductVariationJsonDto dto) {
        try {
            Optional<Product> productOpt = productRepository.findById(dto.productId);
            if (productOpt.isEmpty()) {
                log.warn("Product with ID {} not found for ProductVariation", dto.productId);
                return null;
            }

            ProductVariation variation = ProductVariation.builder()
                    .color(dto.color)
                    .size(dto.size)
                    .price(dto.price)
                    .inventoryQuantity(dto.inventoryQuantity)
                    .soldQuantity(dto.soldQuantity)
                    .isDeleted(dto.isDeleted != null ? dto.isDeleted : false)
                    .product(productOpt.get())
                    .build();

            return variation;
        } catch (Exception e) {
            log.warn("Failed to convert ProductVariationJsonDto to ProductVariation for productId: " + dto.productId);
            return null;
        }
    }

    static class ProductVariationJsonDto {

        public String color;
        public String size;
        public Double price;
        public Integer inventoryQuantity;
        public Integer soldQuantity;
        public Boolean isDeleted;
        public Long productId;
    }
}
