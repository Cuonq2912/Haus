package com.example.haus.repository.criteria;

import com.example.haus.constant.PriceRange;
import com.example.haus.constant.promotion.PromotionStatus;
import com.example.haus.domain.entity.product.Category;
import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.entity.product.ProductVariation;
import com.example.haus.constant.AppConstants;
import jakarta.persistence.criteria.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j(topic = "SEARCH-QUERY-CRITERIA-CONSUMER")
public class SearchQueryCriteriaConsumer<T> implements Consumer<SearchCriteria> {

    private Predicate predicate;
    private CriteriaBuilder criteriaBuilder;
    private Root<T> root;

    @Override
    public void accept(SearchCriteria searchCriteria) {
        Object typedValue = searchCriteria.getValue();

        // --- Filter categoryId ---
        if ("categoryId".equals(searchCriteria.getKey()) && typedValue != null) {
            log.info("Consumer category id");
            Join<Product, Category> categoryJoin = root.join("categories", JoinType.INNER);
            if (typedValue.getClass().isArray()) {
                predicate = criteriaBuilder.and(predicate, categoryJoin.get("id").in((Object[]) typedValue));
            } else {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(categoryJoin.get("id"), typedValue));
            }
            return;
        }

        // --- Filter colors ---
        if ("color".equals(searchCriteria.getKey()) && typedValue != null) {
            log.info("Consumer colors");

            Join<Product, ProductVariation> variationJoin = root.join("productVariations", JoinType.INNER);
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(variationJoin.get("color"), typedValue));
            return;
        }

        // --- Filter material ---
        if ("material".equals(searchCriteria.getKey()) && typedValue != null) {
            log.info("Consumer material");
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("material"), typedValue));
            return;
        }

        // --- Filter PriceRange ---
        if ("priceRange".equalsIgnoreCase(searchCriteria.getKey()) && typedValue != null) {
            log.info("Consumer price range");
            PriceRange range = PriceRange.fromString(typedValue.toString());

            Predicate minPredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("price"), range.getMinPrice());
            Predicate maxPredicate = criteriaBuilder.lessThanOrEqualTo(root.get("price"), range.getMaxPrice());

            predicate = criteriaBuilder.and(predicate, minPredicate, maxPredicate);
            return;
        }

        if ("keyword".equals(searchCriteria.getKey()) && typedValue != null) {
            log.info("Consumer keyword");
            String value = typedValue.toString();
            predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.or(
                            criteriaBuilder.like(root.get("productName"), "%" + value + "%"),
                            criteriaBuilder.like(root.get("description"), "%" + value + "%")
                    ));
            return;
        }

        // --- Chuyển kiểu cho LocalDate và Enum ---
        Class<?> fieldType = root.get(searchCriteria.getKey()).getJavaType();
        if (fieldType.equals(LocalDate.class)) {
            typedValue = LocalDate.parse(typedValue.toString());
        } else if (fieldType.isEnum() && fieldType.equals(PromotionStatus.class)) {
            typedValue = PromotionStatus.fromString((String) typedValue);
        }

        // --- Build Predicate cho mảng ---
        if (typedValue != null && typedValue.getClass().isArray()) {
            Object[] array = (Object[]) typedValue;
            if (array.length > 0) {
                predicate = criteriaBuilder.and(predicate, root.get(searchCriteria.getKey()).in(array));
            }
            return;
        }

        // --- Build Predicate bình thường ---
        switch (searchCriteria.getOperation()) {
            case ">":
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get(searchCriteria.getKey()), (Comparable) typedValue));
                break;
            case "<":
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get(searchCriteria.getKey()), (Comparable) typedValue));
                break;
            case ":":
                if (fieldType.equals(String.class)) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.like(root.get(searchCriteria.getKey()), String.format(AppConstants.STR_FORMAT, typedValue)));
                } else {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.equal(root.get(searchCriteria.getKey()), typedValue));
                }
                break;
        }
    }
}
