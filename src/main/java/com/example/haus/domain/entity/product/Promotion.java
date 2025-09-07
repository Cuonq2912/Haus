package com.example.haus.domain.entity.product;

import com.example.haus.constant.promotion.PromotionStatus;
import com.example.haus.constant.promotion.PromotionType;
import com.example.haus.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "promotions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Promotion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "promotion_code", unique = true, nullable = false)
    String promotionCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    PromotionType type; //Order, Category

    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "min_price_order")
    Float minPriceOrder;

    @Column(name = "max_price_order")
    Float maxPriceOrder;

    @Column(name = "start_date")
    LocalDate startDate;

    @Column(name = "end_date")
    LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    PromotionStatus status;

    @OneToMany(mappedBy = "promotion")
    List<Order> orders;

    @OneToMany(mappedBy = "promotion")
    List<Category> categories;


    // ---------------- Helper methods ----------------

    //Order Promotion
    public void addOrder(Order order) {
        if (!orders.contains(order)) {
            orders.add(order);
            order.setPromotion(this);
        }
    }

    public void removeOrder(Order order) {
        if (orders.contains(order)) {
            orders.remove(order);
            order.setPromotion(null);
        }
    }

    // CategoryPromotion
    public void addCategory(Category category) {
        if (!categories.contains(category)) {
            categories.add(category);
            category.setPromotion(this);
        }
    }

    public void removeCategory(Category category) {
        if (categories.contains(category)) {
            categories.remove(category);
            category.setPromotion(null);
        }
    }
}
