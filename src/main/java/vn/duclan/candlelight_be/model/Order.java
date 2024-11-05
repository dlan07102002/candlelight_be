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
import lombok.Data;

@Data
@Entity
@Table(name = "orders")
public class Order {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "order_id")
        private int orderId;

        @Column(name = "created_at")
        private Date createdAt;

        @Column(name = "order_address", length = 512)
        private String orderAddress;

        @Column(name = "delivery_address", length = 512)
        private String deliveryAddress;

        @Column(name = "delivery_cost")
        private double deliveryCost;

        @Column(name = "payment_cost")
        private double paymentCost;

        @Column(name = "total_price")
        private double totalPrice;

        @Column(name = "delivery_status")
        // "Pending", //created but not delivery
        // "Shipped",
        // "In Transit",
        // "Out for Delivery",
        // "Delivered",
        // "Returned",
        // "Canceled",
        // "Delayed"
        private String deliveryStatus;

        // [Pending, Paid, Failed, Refunded, Canceled, Processing, Completed]
        @Column(name = "payment_status")
        private String paymentStatus;

        @Transient
        private int userId;

        @ManyToOne(fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JoinColumn(name = "user_id", nullable = false)

        private User user;

        @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        private List<OrderDetail> orderDetailList;

        @ManyToOne(fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JoinColumn(name = "delivery_method_id")
        private DeliveryMethod deliveryMethod;

        @ManyToOne(fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JoinColumn(name = "payment_method_id")
        private PaymentMethod paymentMethod;
}
