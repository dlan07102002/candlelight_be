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
import lombok.Data;

@Data
@Entity
@Table(name = "order_details")
public class OrderDetail {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "order_detail_id")
        private long orderDetailId;

        @Column(name = "quantity")
        private int quantity;

        @Column(name = "sell_price")
        private double sellPrice;

        @ManyToOne(fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JoinColumn(name = "product_id", nullable = false)
        // JsonIgnore annotation is used to serialize or deserialize specific attribute
        // when obj convert to Json and vice versa
        // Put this here to avoid recursion
        @JsonIgnore
        private Product product;

        @ManyToOne(fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JsonIgnore
        @JoinColumn(name = "order_id", nullable = false)
        private Order order;

}
