package com.example.haus.repository;

import com.example.haus.domain.entity.product.Category;
import com.example.haus.domain.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByCategoryName(String categoryName);

    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

    List<Category> findByParentCategoryIsNotNull();

}
