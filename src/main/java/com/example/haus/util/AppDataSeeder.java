package com.example.haus.util;

import com.example.haus.domain.entity.product.Category;
import com.example.haus.repository.CategoryRepository;
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

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppDataSeeder implements ApplicationRunner {

    CategoryRepository categoryRepository;

    ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        seedCategory();
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
}
