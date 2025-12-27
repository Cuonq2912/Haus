package com.example.haus.domain.entity.product;

import com.example.haus.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Entity
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Integer quantity;

    @Column(nullable = false)
    Double priceAtSale;

    @Column(nullable = false)
    String snapshotProductCode;

    @Column(nullable = false)
    String snapshotProductName;

    @Lob
    @Column(columnDefinition = "TEXT")
    String snapshotDescription;

    @Column(length = 100)
    String snapshotMaterial;

    @Column(nullable = false)
    String snapshotColor;

    @Column(nullable = false)
    String snapshotSize;

    @Column
    String snapshotImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    ProductVariation productVariation;
}
