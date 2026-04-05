package com.example.haus.domain.entity.product;

import com.example.haus.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;

import java.util.List;

@Entity
@Table(name = "categories")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Category> subCategories;

    @Column(columnDefinition = "TEXT")
    String description;

    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    List<Product> products = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "category")
    Promotion promotion;
}
