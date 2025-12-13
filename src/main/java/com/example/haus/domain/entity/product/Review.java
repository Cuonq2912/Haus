package com.example.haus.domain.entity.product;

import com.example.haus.constant.CommonConstant;
import com.example.haus.domain.entity.BaseEntity;
import com.example.haus.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "reviews")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Integer rating;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    String content;

    @Column(name = "is_hidden")
    @Builder.Default
    Boolean isHidden = CommonConstant.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = true)
    OrderItem orderItem;

}

