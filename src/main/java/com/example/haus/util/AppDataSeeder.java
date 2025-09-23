package com.example.haus.util;

import com.example.haus.domain.entity.product.Category;
import com.example.haus.domain.entity.product.Product;
import com.example.haus.repository.CategoryRepository;
import com.example.haus.repository.ProductRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppDataSeeder implements ApplicationRunner {

    CategoryRepository categoryRepository;

    ProductRepository productRepository;

    ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        seedCategory();
        seedProduct();
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

            List<ProductJsonDto> productDtosFromJson = objectMapper.readValue(is, new TypeReference<>() {
            });

            if (productsFromDB.isEmpty()) {
                for (ProductJsonDto dto : productDtosFromJson) {
                    Product product = convertToProduct(dto);
                    if (product != null) {
                        productRepository.save(product);
                    }
                }
            } else {
                for (ProductJsonDto dto : productDtosFromJson) {
                    if (!productRepository.existsByProductCode(dto.productCode)) {
                        Product product = convertToProduct(dto);
                        if (product != null) {
                            productRepository.save(product);
                        }
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
            Product product = Product.builder()
                    .productCode(dto.productCode)
                    .productName(dto.productName)
                    .price(dto.price)
                    .description(dto.description)
                    .detailDescription(dto.detailDescription)
                    .inventoryQuantity(dto.inventoryQuantity)
                    .isDeleted(false)
                    .build();

            return product;

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
        public Integer inventoryQuantity;
        public List<String> categories;
    }
}
