package com.example.haus.domain.entity.product;

import com.example.haus.constant.PromotionStatus;
import com.example.haus.domain.entity.BaseEntity;
import com.example.haus.domain.entity.user.Role;
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
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", nullable = false)
    PromotionStatus status;

    @OneToMany(mappedBy = "promotion")
    List<Order> orders;

    @OneToMany(mappedBy = "promotion")
    List<Category> categories;
}
