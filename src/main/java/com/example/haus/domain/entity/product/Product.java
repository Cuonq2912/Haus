package com.example.haus.domain.entity.product;

import com.example.haus.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String productName;

    @Column(nullable = false)
    Double price;

    @Lob
    @Column(columnDefinition = "TEXT")
    String description;

    @Column(nullable = false)
    Integer inventoryQuantity;

    @ManyToMany
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    List<Category> categories;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    List<Review> reviews;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    List<CartItem> cartItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    List<OrderItem> orderItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    List<ProductVariation> productVariations;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    @JsonIgnore
    Set<Media> medias = new HashSet<>();

}
