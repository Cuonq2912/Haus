package com.example.haus.repository;

import com.example.haus.domain.entity.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByCategoryName(String categoryName);

    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

    List<Category> findByParentCategoryIsNotNull();
}
