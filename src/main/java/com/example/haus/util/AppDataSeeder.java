package com.example.haus.util;

import com.example.haus.constant.MediaType;
import com.example.haus.domain.entity.product.Category;
import com.example.haus.domain.entity.product.Media;
import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.entity.product.ProductVariation;
import com.example.haus.repository.CategoryRepository;
import com.example.haus.repository.MediaRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppDataSeeder implements ApplicationRunner {

    CategoryRepository categoryRepository;
    MediaRepository mediaRepository;
    ProductRepository productRepository;
    ProductVariationRepository productVariationRepository;
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

            List<ProductJsonDto> productDtosFromJson = objectMapper.readValue(is, new TypeReference<>() {
            });

            int productCount = 0;
            int variationCount = 0;
            int mediaCount = 0;

            for (ProductJsonDto dto : productDtosFromJson) {
                if (!productRepository.existsByProductCode(dto.productCode)) {
                    Product product = convertToProduct(dto);
                    if (product != null) {
                        if (dto.categories != null) {
                            for (String categoryName : dto.categories) {
                                Optional<Category> categoryOpt = categoryRepository
                                        .findByCategoryNameIgnoreCase(categoryName);
                                if (categoryOpt.isPresent()) {
                                    product.addCategory(categoryOpt.get());
                                } else {
                                    log.warn("Category '{}' not found for Product: {}", categoryName, dto.productCode);
                                }
                            }
                        }

                        product = productRepository.save(product);
                        productCount++;

                        List<Media> mediaList = new ArrayList<>();
                        if (dto.images != null) {
                            for (String imageUrl : dto.images) {
                                Media media = Media.builder().url(imageUrl).type(MediaType.IMAGE).product(product)
                                        .build();
                                media = mediaRepository.save(media);
                                mediaList.add(media);
                                mediaCount++;
                            }
                        }

                        if (dto.variations != null) {
                            for (VariationJsonDto variationDto : dto.variations) {
                                ProductVariation variation = ProductVariation.builder().color(variationDto.color)
                                        .size(variationDto.size != null ? variationDto.size : "")
                                        .price(variationDto.price).inventoryQuantity(variationDto.inventoryQuantity)
                                        .soldQuantity(variationDto.soldQuantity != null ? variationDto.soldQuantity : 0)
                                        .isDeleted(variationDto.isDeleted != null ? variationDto.isDeleted : false)
                                        .imageIndex(variationDto.imageIndex).product(product).build();

                                variation = productVariationRepository.save(variation);
                                variationCount++;

                                if (variationDto.imageIndex != null && variationDto.imageIndex < mediaList.size()) {
                                    Media linkedMedia = mediaList.get(variationDto.imageIndex);
                                    linkedMedia.setProductVariation(variation);
                                    mediaRepository.save(linkedMedia);
                                }
                            }
                        }
                    }
                }
            }

            log.info("Seeding completed: {} products, {} variations, {} media files", productCount, variationCount,
                    mediaCount);

        } catch (IOException e) {
            log.warn("Seeding product from JSON fail: " + e.getMessage(), e);
        }
    }

    private Product convertToProduct(ProductJsonDto dto) {
        try {
            return Product.builder().productCode(dto.productCode).productName(dto.productName).price(dto.price)
                    .description(dto.description).detailDescription(dto.detailDescription)
                    .inventoryQuantity(dto.inventoryQuantity).material(dto.material)
                    .soldQuantity(dto.soldQuantity != null ? dto.soldQuantity : 0).isDeleted(false).build();
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
        public List<String> images;
        public Integer mainImageIndex;
        public List<VariationJsonDto> variations;
    }

    static class VariationJsonDto {
        public String color;
        public String size;
        public Double price;
        public Integer inventoryQuantity;
        public Integer soldQuantity;
        public Boolean isDeleted;
        public Integer imageIndex;
    }
}
