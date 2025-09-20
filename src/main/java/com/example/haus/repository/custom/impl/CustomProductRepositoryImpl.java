package com.example.haus.repository.custom.impl;

import com.example.haus.constant.PriceRange;
import com.example.haus.constant.ProductColor;
import com.example.haus.domain.dto.request.product.ProductFilterRequestDto;
import com.example.haus.domain.entity.product.Category;
import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.entity.product.ProductVariation;
import com.example.haus.repository.custom.CustomProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class CustomProductRepositoryImpl implements CustomProductRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Page<Product> filterProducts(ProductFilterRequestDto filterRequest, Pageable pageable) {
    log.info("Filtering products with request: {}", filterRequest);

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    // truy vấn đếm
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Product> countRoot = countQuery.from(Product.class);
    List<Predicate> countPredicates = buildPredicates(filterRequest, cb, countRoot);

    countQuery.select(cb.countDistinct(countRoot));
    if (!countPredicates.isEmpty()) {
      countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
    }

    Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
    log.info("Total elements: {}", totalElements);

    if (totalElements == 0) {
      return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    // truy vấn chính
    CriteriaQuery<Product> cq = cb.createQuery(Product.class);
    Root<Product> root = cq.from(Product.class);
    List<Predicate> predicates = buildPredicates(filterRequest, cb, root);

    cq.select(root).distinct(true);
    if (!predicates.isEmpty()) {
      cq.where(cb.and(predicates.toArray(new Predicate[0])));
    }

    List<Product> results = entityManager.createQuery(cq)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();
    log.info("Result size: {}", results.size());

    return new PageImpl<>(results, pageable, totalElements);
  }

  private List<Predicate> buildPredicates(ProductFilterRequestDto filterRequest,
                                          CriteriaBuilder cb,
                                          Root<Product> root) {
    List<Predicate> predicates = new ArrayList<>();
    // keyword
    if (StringUtils.hasText(filterRequest.getKeyword())) {
      String keyword = "%" + filterRequest.getKeyword().trim().toLowerCase() + "%";

      Expression<String> descriptionExpr = root.get("description").as(String.class);
      Expression<String> detailDescExpr = root.get("detailDescription").as(String.class);

      predicates.add(cb.or(
              cb.like(cb.lower(root.get("productName")), keyword),
              cb.like(cb.lower(descriptionExpr), keyword),
              cb.like(cb.lower(detailDescExpr), keyword)
      ));
    }

    // price range
    if (filterRequest.getPriceRanges() != null && !filterRequest.getPriceRanges().isEmpty()) {
      List<Predicate> pricePredicates = new ArrayList<>();
      for (PriceRange priceRange : filterRequest.getPriceRanges()) {
        if (priceRange == null) continue;
        switch (priceRange) {
          case UNDER_1M -> pricePredicates.add(cb.lessThan(root.get("price"), 1_000_000.0));
          case FROM_1M_TO_3M -> pricePredicates.add(cb.between(root.get("price"), 1_000_000.0, 3_000_000.0));
          case FROM_3M_TO_6M -> pricePredicates.add(cb.between(root.get("price"), 3_000_000.0, 6_000_000.0));
          case FROM_6M_TO_8M -> pricePredicates.add(cb.between(root.get("price"), 6_000_000.0, 8_000_000.0));
          case ABOVE_8M -> pricePredicates.add(cb.greaterThanOrEqualTo(root.get("price"), 8_000_000.0));
        }
      }
      if (!pricePredicates.isEmpty()) {
        predicates.add(cb.or(pricePredicates.toArray(new Predicate[0])));
      }
    }

    // colors (subquery)
    if (filterRequest.getColors() != null && !filterRequest.getColors().isEmpty()) {
      List<String> colorNames = filterRequest.getColors().stream()
              .filter(color -> color != null)
              .map(ProductColor::getDisplayName)
              .filter(StringUtils::hasText)
              .toList();
      if (!colorNames.isEmpty()) {
        Subquery<Long> subquery = cb.createQuery().subquery(Long.class);
        Root<ProductVariation> subRoot = subquery.from(ProductVariation.class);
        subquery.select(subRoot.get("product").get("id"))
                .where(
                        cb.equal(subRoot.get("product"), root),
                        subRoot.get("color").in(colorNames)
                );
        predicates.add(cb.exists(subquery));
      }
    }

    // category id
    if (filterRequest.getCategoryId() != null) {
      Subquery<Long> subquery = cb.createQuery().subquery(Long.class);
      Root<Product> subRoot = subquery.from(Product.class);
      Join<Product, Category> subCategoryJoin = subRoot.join("categories");
      subquery.select(subRoot.get("id"))
              .where(cb.and(
                      cb.equal(subRoot.get("id"), root.get("id")),
                      cb.equal(subCategoryJoin.get("id"), filterRequest.getCategoryId())
              ));
      predicates.add(cb.exists(subquery));
    }

    log.debug("Predicates: {}", predicates);
    return predicates;
  }
}
