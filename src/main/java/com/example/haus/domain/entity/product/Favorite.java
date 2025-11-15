package com.example.haus.domain.entity.product;

import com.example.haus.domain.entity.BaseEntity;
import com.example.haus.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Entity
@Table(
        name = "favorites",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_product",
                        columnNames = {"user_id", "product_id"}
                )
        },
        indexes = {
                @Index(name = "idx_favorites_user_id", columnList = "user_id"),
                @Index(name = "idx_favorites_product_id", columnList = "product_id"),
                @Index(name = "idx_favorites_created_at", columnList = "created_at DESC")
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Favorite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @JoinColumn(name = "product_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    Product product;

    public static Favorite of(User user, Product product) {
        return Favorite.builder()
                .user(user)
                .product(product)
                .build();
    }


}



