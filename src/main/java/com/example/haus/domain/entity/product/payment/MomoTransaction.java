package com.example.haus.domain.entity.product.payment;

import com.example.haus.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "momo_transactions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoTransaction extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", insertable = false, updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    String id;

    @Column(name = "momo_order_id", unique = true, nullable = false, length = 100)
    String momoOrderId;

    @Column(name = "momo_request_id", length = 100)
    String momoRequestId;

    @Column(name = "momo_trans_id", length = 100)
    String momoTransId;

    @Column(name = "order_id", nullable = false)
    Long orderId;

    @Column(name = "payment_id", nullable = false, columnDefinition = "CHAR(36)")
    String paymentId;

    @Column(name = "amount", nullable = false)
    Double amount;

    @Column(name = "result_code", length = 10)
    String resultCode;

    @Column(name = "message", length = 500)
    String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    PaymentStatus status;

}
