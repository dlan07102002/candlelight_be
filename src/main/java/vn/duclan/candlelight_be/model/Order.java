package vn.duclan.candlelight_be.model;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.duclan.candlelight_be.model.enums.DeliveryStatus;
import vn.duclan.candlelight_be.model.enums.PaymentStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

        /*
         * PENDING: Đơn hàng chưa được xử lý.
         * SHIPPED: Đơn hàng đã được giao cho đơn vị vận chuyển.
         * DELIVERED: Đơn hàng đã được giao thành công.
         * FAILED: Giao hàng thất bại hoặc bị lỗi.
         */
        @Enumerated(EnumType.STRING)
        @Column(name = "delivery_status")
        DeliveryStatus deliveryStatus;

        /*
         * PENDING: Thanh toán đang chờ xử lý.
         * SUCCESS: Thanh toán thành công.
         * FAILED: Thanh toán thất bại.
         */
        @Enumerated(EnumType.STRING)
        @Column(name = "payment_status")
        PaymentStatus paymentStatus;

        @Transient
        // Can send from client but not seen in response
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        int userId;

        @Transient
        int paymentMethodId;

        @Transient
        int deliveryMethodId;

        @ManyToOne(fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JoinColumn(name = "user_id", nullable = true)
        @JsonIgnore
        User user;

        @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        List<OrderDetail> orderDetailList;

        @ManyToOne(fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JoinColumn(name = "delivery_method_id")
        @JsonIgnore
        DeliveryMethod deliveryMethod;

        @ManyToOne(fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JoinColumn(name = "payment_method_id")
        @JsonIgnore
        PaymentMethod paymentMethod;

        @PostLoad
        private void assignId() {
                if (user != null) {
                        this.userId = user.getUserId();
                }
                if (paymentMethod != null) {
                        this.paymentMethodId = paymentMethod.getPaymentMethodId();
                }
                if (deliveryMethod != null) {
                        this.deliveryMethodId = deliveryMethod.getDeliveryMethodId();
                }
        }

}
