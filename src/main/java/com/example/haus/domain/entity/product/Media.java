package com.example.haus.domain.entity.product;

import com.example.haus.constant.MediaType;
import com.example.haus.domain.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity(name = "medias")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Media extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String url;

    @Enumerated(EnumType.STRING)
    MediaType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnore
    Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variation_id")
    @JsonIgnore
    ProductVariation productVariation;

}
