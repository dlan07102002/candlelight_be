package vn.duclan.candlelight_be.model;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "orders")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "order_id")
        int orderId;

        @Column(name = "created_at")
        Date createdAt;

        @Column(name = "order_address", length = 512)
        String orderAddress;

        @Column(name = "delivery_address", length = 512)
        String deliveryAddress;

        @Column(name = "delivery_cost")
        double deliveryCost;

        @Column(name = "payment_cost")
        double paymentCost;

        @Column(name = "total_price")
        double totalPrice;

        @Column(name = "delivery_status")
        // "Pending", //created but not delivery
        // "Shipped",
        // "In Transit",
        // "Out for Delivery",
        // "Delivered",
        // "Returned",
        // "Canceled",
        // "Delayed"
        String deliveryStatus;

        // [Pending, Paid, Failed, Refunded, Canceled, Processing, Completed]
        @Column(name = "payment_status")
        String paymentStatus;

        @Transient
        int userId;

        @ManyToOne(fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JoinColumn(name = "user_id", nullable = false)

        User user;

        @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        List<OrderDetail> orderDetailList;

        @ManyToOne(fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JoinColumn(name = "delivery_method_id")
        DeliveryMethod deliveryMethod;

        @ManyToOne(fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JoinColumn(name = "payment_method_id")
        PaymentMethod paymentMethod;
}
