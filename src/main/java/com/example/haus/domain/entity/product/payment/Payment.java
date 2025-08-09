package com.example.haus.domain.entity.product.payment;

import com.example.haus.domain.entity.BaseEntity;
import com.example.haus.domain.entity.product.Order;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;

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

  LocalDate paymente;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_method_id", nullable = false)
  PaymentMethod paymentMethod;


}
