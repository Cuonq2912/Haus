package com.example.haus.domain.entity.product;

import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.entity.BaseEntity;
import com.example.haus.domain.entity.address.Address;
import com.example.haus.domain.entity.product.payment.Payment;
import com.example.haus.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String orderNumber;

    Double shippingFee;

    Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    OrderStatus status;

    LocalDate orderDate;

    LocalDate deliveryDate;

    @Column(length = 500)
    String note;

    @Column(length = 100)
    String recipientName;

    @Column(length = 20)
    String recipientPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id")
    Address shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    List<OrderItem> orderItems;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    Promotion promotion;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    List<Address> addresses;

    // ---------------- Helper methods ----------------

    public void addOrderItem(OrderItem orderItem) {
        if (!orderItems.contains(orderItem)) {
            orderItems.add(orderItem);
            orderItem.setOrder(this);
        }
    }

    public void removeOrderItem(OrderItem orderItem) {
        if (orderItems.contains(orderItem)) {
            orderItems.remove(orderItem);
            orderItem.setOrder(null);
        }
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
        if (payment != null) {
            payment.setOrder(this);
        }
    }
}
