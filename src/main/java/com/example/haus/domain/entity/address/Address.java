package com.example.haus.domain.entity.address;

import com.example.haus.constant.CommonConstant;
import com.example.haus.domain.entity.BaseEntity;
import com.example.haus.domain.entity.product.Order;
import com.example.haus.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "address")
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    Long id;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "district")
    private String district;

    @Column(name = "commune")
    private String commune;

    @Column(name = "detail_address")
    private String detailAddress;

    @Builder.Default
    @Column(name = "is_selected")
    private Boolean isSelected = CommonConstant.FALSE;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = CommonConstant.FALSE;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
