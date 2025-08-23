package com.example.haus.domain.entity.user;

import com.example.haus.constant.CommonConstant;
import com.example.haus.domain.entity.BaseEntity;
import com.example.haus.domain.entity.address.Address;
import com.example.haus.domain.entity.news.CommentNews;
import com.example.haus.domain.entity.news.News;
import com.example.haus.domain.entity.product.Cart;
import com.example.haus.domain.entity.product.Order;
import com.example.haus.domain.entity.product.Review;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Nationalized;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(insertable = false, updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    String id;

    @Column(nullable = false, updatable = false, unique = true)
    String username;

    @Column(nullable = false)
    @JsonIgnore
    String password;

    String firstName;

    String lastName;

    Date dateOfBirth;

    String avatarLink;

    @Column(nullable = false)
    String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Gender gender;

    String phone;

    @Builder.Default
    Boolean isLock = CommonConstant.FALSE;

    @Builder.Default
    Boolean isActive = CommonConstant.TRUE;

    @Builder.Default
    Boolean isDeleted = CommonConstant.FALSE;

    @Nationalized
    String nationality;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Order> orders;

    @OneToMany(mappedBy = "author")
    List<News> authoredNews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<CommentNews> newsComments;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    Address address;
}
