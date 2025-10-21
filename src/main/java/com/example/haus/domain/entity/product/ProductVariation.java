package com.example.haus.domain.entity.product;

import com.example.haus.domain.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "product_variations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String color;

    @Column(nullable = false)
    String size;

    @Column(nullable = false)
    Double price;

    @Column(nullable = false)
    Integer inventoryQuantity;

    @Column(nullable = false)
    Integer soldQuantity;

    @Column()
    Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "productVariation", fetch = FetchType.LAZY)
    @JsonIgnore
    Media media;

    @OneToMany(mappedBy = "productVariation", cascade = CascadeType.ALL)
    List<OrderItem> orderItems;

    @OneToMany(mappedBy = "productVariation", cascade = CascadeType.ALL)
    List<CartItem> cartItems;
}