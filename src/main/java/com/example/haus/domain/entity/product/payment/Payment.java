package com.example.haus.domain.entity.product.payment;

import com.example.haus.domain.entity.BaseEntity;
import com.example.haus.domain.entity.product.Order;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", insertable = false, updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    String id;

    Long amount;

    @Enumerated(EnumType.STRING)
    PaymentGateway gateway;

    @Enumerated(EnumType.STRING)
    PaymentType type;

    @Enumerated(EnumType.STRING)
    PaymentStatus status;

    Date expireAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

}
