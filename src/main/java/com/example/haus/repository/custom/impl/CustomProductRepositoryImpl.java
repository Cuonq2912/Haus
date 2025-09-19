package com.example.haus.repository.custom.impl;

import com.example.haus.constant.PriceRange;
import com.example.haus.constant.ProductColor;
import com.example.haus.constant.ProductStyle;
import com.example.haus.domain.dto.request.product.ProductFilterRequestDto;
import com.example.haus.domain.entity.product.Category;
import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.entity.product.ProductVariation;
import com.example.haus.repository.custom.CustomProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomProductRepositoryImpl implements CustomProductRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Page<Product> filterProducts(ProductFilterRequestDto filterRequest, Pageable pageable) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Product> query = cb.createQuery(Product.class);
    Root<Product> product = query.from(Product.class);

    List<Predicate> predicates = new ArrayList<>();

    predicates.add(cb.isFalse(product.get("isDeleted")));

    if (filterRequest.getKeyword() != null && !filterRequest.getKeyword().trim().isEmpty()) {
      String keyword = "%" + filterRequest.getKeyword().toLowerCase().trim() + "%";
      Predicate keywordPredicate = cb.or(
          cb.like(cb.lower(product.get("productName")), keyword.toLowerCase()),
          cb.like(cb.lower(product.get("productDescription")), keyword.toLowerCase()),
          cb.like(cb.lower(product.get("productDetailDescription")), keyword.toLowerCase()));
      predicates.add(keywordPredicate);
    }

    if (filterRequest.getPriceRanges() != null && !filterRequest.getPriceRanges().isEmpty()) {
      List<Predicate> pricePredicates = new ArrayList<>();

      for (PriceRange priceRange : filterRequest.getPriceRanges()) {
        Predicate pricePredicate = null;

        switch (priceRange) {
          case UNDER_1M:
            pricePredicate = cb.lessThan(product.get("price"), 1000000.0);
            break;
          case FROM_1M_TO_3M:
            pricePredicate = cb.and(
                cb.greaterThanOrEqualTo(product.get("price"), 1000000.0),
                cb.lessThan(product.get("price"), 2000000.0));
            break;
          case FROM_3M_TO_6M:
            pricePredicate = cb.and(
                cb.greaterThanOrEqualTo(product.get("price"), 2000000.0),
                cb.lessThan(product.get("price"), 5000000.0));
            break;
          case FROM_6M_TO_8M:
            pricePredicate = cb.and(
                cb.greaterThanOrEqualTo(product.get("price"), 5000000.0),
                cb.lessThan(product.get("price"), 8000000.0));
            break;
          case ABOVE_8M:
            pricePredicate = cb.greaterThanOrEqualTo(product.get("price"), 8000000.0);
            break;
        }

        if (pricePredicate != null) {
          pricePredicates.add(pricePredicate);
        }
      }

      if (!pricePredicates.isEmpty()) {
        predicates.add(cb.or(pricePredicates.toArray(new Predicate[0])));
      }
    }

    if (filterRequest.getColors() != null && !filterRequest.getColors().isEmpty()) {
      Join<Product, ProductVariation> variationJoin = product.join("productVariations", JoinType.INNER);
      List<String> colorNames = filterRequest.getColors().stream()
          .map(ProductColor::getDisplayName)
          .toList();
      predicates.add(variationJoin.get("color").in(colorNames));
    }

    if (filterRequest.getStyles() != null && !filterRequest.getStyles().isEmpty()) {
      Join<Product, Category> categoryJoin = product.join("categories", JoinType.INNER);
      List<String> styleNames = filterRequest.getStyles().stream()
          .map(ProductStyle::getDisplayName)
          .toList();
      predicates.add(categoryJoin.get("categoryName").in(styleNames));
    }

    if (!predicates.isEmpty()) {
      query.where(cb.and(predicates.toArray(new Predicate[0])));
    }

    query.select(product).distinct(true);

    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Product> countRoot = countQuery.from(Product.class);

    List<Predicate> countPredicates = new ArrayList<>();
    countPredicates.add(cb.isFalse(countRoot.get("isDeleted")));

    if (filterRequest.getKeyword() != null && !filterRequest.getKeyword().trim().isEmpty()) {
      String keyword = "%" + filterRequest.getKeyword().toLowerCase().trim() + "%";
      Predicate keywordPredicate = cb.or(
          cb.like(cb.lower(countRoot.get("productName")), keyword),
          cb.like(cb.lower(countRoot.get("productDescription")), keyword),
          cb.like(cb.lower(countRoot.get("productDetailDescription")), keyword));
      countPredicates.add(keywordPredicate);
    }

    if (filterRequest.getPriceRanges() != null && !filterRequest.getPriceRanges().isEmpty()) {
      List<Predicate> pricePredicates = new ArrayList<>();

      for (PriceRange priceRange : filterRequest.getPriceRanges()) {
        Predicate pricePredicate = null;

        switch (priceRange) {
          case UNDER_1M:
            pricePredicate = cb.lessThan(countRoot.get("price"), 1000000.0);
            break;
          case FROM_1M_TO_3M:
            pricePredicate = cb.and(
                cb.greaterThanOrEqualTo(countRoot.get("price"), 1000000.0),
                cb.lessThan(countRoot.get("price"), 2000000.0));
            break;
          case FROM_3M_TO_6M:
            pricePredicate = cb.and(
                cb.greaterThanOrEqualTo(countRoot.get("price"), 2000000.0),
                cb.lessThan(countRoot.get("price"), 5000000.0));
            break;
          case FROM_6M_TO_8M:
            pricePredicate = cb.and(
                cb.greaterThanOrEqualTo(countRoot.get("price"), 5000000.0),
                cb.lessThan(countRoot.get("price"), 8000000.0));
            break;
          case ABOVE_8M:
            pricePredicate = cb.greaterThanOrEqualTo(countRoot.get("price"), 8000000.0);
            break;
        }

        if (pricePredicate != null) {
          pricePredicates.add(pricePredicate);
        }
      }

      if (!pricePredicates.isEmpty()) {
        countPredicates.add(cb.or(pricePredicates.toArray(new Predicate[0])));
      }
    }

    if (filterRequest.getColors() != null && !filterRequest.getColors().isEmpty()) {
      Join<Product, ProductVariation> variationJoin = countRoot.join("productVariations", JoinType.INNER);
      List<String> colorNames = filterRequest.getColors().stream()
          .map(ProductColor::getDisplayName)
          .toList();
      countPredicates.add(variationJoin.get("color").in(colorNames));
    }

    if (filterRequest.getStyles() != null && !filterRequest.getStyles().isEmpty()) {
      Join<Product, Category> categoryJoin = countRoot.join("categories", JoinType.INNER);
      List<String> styleNames = filterRequest.getStyles().stream()
          .map(ProductStyle::getDisplayName)
          .toList();
      countPredicates.add(categoryJoin.get("categoryName").in(styleNames));
    }

    countQuery.select(cb.countDistinct(countRoot));
    if (!countPredicates.isEmpty()) {
      countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
    }

    Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

    List<Product> results = entityManager.createQuery(query)
        .setFirstResult((int) pageable.getOffset())
        .setMaxResults(pageable.getPageSize())
        .getResultList();

    return new PageImpl<>(results, pageable, totalElements);
  }
}