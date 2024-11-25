package vn.duclan.candlelight_be.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "order_details")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetail {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "order_detail_id")
        long orderDetailId;

        @Column(name = "quantity")
        int quantity;

        @Column(name = "sell_price")
        double sellPrice;

        @Transient // Trường này sẽ không được lưu trong cơ sở dữ liệu
        int productId;

        @ManyToOne(fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JoinColumn(name = "product_id", nullable = false)
        // JsonIgnore annotation is used to serialize or deserialize specific attribute
        // when obj convert to Json and vice versa
        // Put this here to avoid recursion
        @JsonIgnore
        Product product;

        @Transient
        int orderId;

        @Transient
        int userId;
        @ManyToOne(fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JsonIgnore
        @JoinColumn(name = "order_id", nullable = false)
        Order order;

}
