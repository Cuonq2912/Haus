package com.example.haus.domain.entity.product;

import com.example.haus.constant.promotion.PromotionStatus;
import com.example.haus.constant.promotion.PromotionType;
import com.example.haus.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @Column(name = "promotion_code", unique = true)
    String promotionCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    PromotionType type; //Order, Category

    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "min_price_order", nullable = false)
    Float minPriceOrder;

    @Column(name = "max_price_order", nullable = false)
    Float maxPriceOrder;

    @Column(name = "start_date")
    LocalDate startDate;

    @Column(name = "end_date")
    LocalDate endDate;

    @Column(name = "discount_percent")
    Float discountPercent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    PromotionStatus status;

    @OneToMany(mappedBy = "promotion")
    List<Order> orders;

    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;


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

}
