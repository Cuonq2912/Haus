package com.example.haus.repository.criteria;

import com.example.haus.constant.AppConstants;
import com.example.haus.constant.PriceRange;
import com.example.haus.constant.promotion.PromotionStatus;
import com.example.haus.domain.entity.product.Category;
import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.entity.product.ProductVariation;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    public void accept(SearchCriteria sc) {
        Object value = sc.getValue();
        if (value == null) {
            return;
        }

        // 1) Handle các key đặc biệt trước (mỗi cái 1 method)
        if (handleCategoryId(sc, value)) return;
        if (handleColor(sc, value)) return;
        if (handleMaterial(sc, value)) return;
        if (handlePriceRange(sc, value)) return;
        if (handleKeyword(sc, value)) return;

        // 2) Default: convert type + build predicate theo operation/array
        handleDefault(sc, value);
    }

    private boolean handleCategoryId(SearchCriteria sc, Object value) {
        if (!"categoryId".equals(sc.getKey())) return false;

        log.info("Consumer category id");
        Join<Product, Category> categoryJoin = root.join("categories", JoinType.INNER);

        if (value.getClass().isArray()) {
            predicate = criteriaBuilder.and(predicate, categoryJoin.get("id").in((Object[]) value));
        } else {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(categoryJoin.get("id"), value));
        }
        return true;
    }

    private boolean handleColor(SearchCriteria sc, Object value) {
        if (!"color".equals(sc.getKey())) return false;

        log.info("Consumer colors");
        Join<Product, ProductVariation> variationJoin = root.join("productVariations", JoinType.INNER);
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(variationJoin.get("color"), value));
        return true;
    }

    private boolean handleMaterial(SearchCriteria sc, Object value) {
        if (!"material".equals(sc.getKey())) return false;

        log.info("Consumer material");
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("material"), value));
        return true;
    }

    private boolean handlePriceRange(SearchCriteria sc, Object value) {
        if (!"priceRange".equalsIgnoreCase(sc.getKey())) return false;

        log.info("Consumer price range");
        PriceRange range = PriceRange.fromString(value.toString());

        Predicate minPredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("price"), range.getMinPrice());
        Predicate maxPredicate = criteriaBuilder.lessThanOrEqualTo(root.get("price"), range.getMaxPrice());

        predicate = criteriaBuilder.and(predicate, minPredicate, maxPredicate);
        return true;
    }

    private boolean handleKeyword(SearchCriteria sc, Object value) {
        if (!"keyword".equals(sc.getKey())) return false;

        log.info("Consumer keyword");
        String keyword = value.toString();

        predicate = criteriaBuilder.and(
                predicate,
                criteriaBuilder.or(
                        criteriaBuilder.like(root.get("productName"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.get("description"), "%" + keyword + "%")
                )
        );
        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void handleDefault(SearchCriteria sc, Object rawValue) {
        String key = sc.getKey();
        Class<?> fieldType = root.get(key).getJavaType();

        Object typedValue = convertValue(fieldType, rawValue);

        // Nếu là array -> dùng IN
        if (typedValue != null && typedValue.getClass().isArray()) {
            Object[] arr = (Object[]) typedValue;
            if (arr.length > 0) {
                predicate = criteriaBuilder.and(predicate, root.get(key).in(arr));
            }
            return;
        }

        switch (sc.getOperation()) {
            case ">":
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get(key), (Comparable) typedValue)
                );
                return;

            case "<":
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get(key), (Comparable) typedValue)
                );
                return;

            case ":":
                predicate = criteriaBuilder.and(predicate, buildEqualsOrLikePredicate(key, fieldType, typedValue));
                return;

            default:
        }
    }

    private Object convertValue(Class<?> fieldType, Object raw) {
        if (raw == null) return null;

        if (fieldType.equals(LocalDate.class)) {
            return LocalDate.parse(raw.toString());
        }

        if (fieldType.equals(PromotionStatus.class)) {
            return PromotionStatus.fromString(raw.toString());
        }

        return raw;
    }

    private Predicate buildEqualsOrLikePredicate(String key, Class<?> fieldType, Object typedValue) {
        if (fieldType.equals(String.class)) {
            return criteriaBuilder.like(
                    root.get(key),
                    String.format(AppConstants.STR_FORMAT, typedValue)
            );
        }
        return criteriaBuilder.equal(root.get(key), typedValue);
    }

}
