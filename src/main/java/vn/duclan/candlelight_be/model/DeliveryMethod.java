package vn.duclan.candlelight_be.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "delivery_methods")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_method_id")
    int deliveryMethodId;

    @Column(name = "delivery_method_name")
    String deliveryMethodName;

    @Column(name = "description")
    String description;

    @Column(name = "delivery_cost")
    double deliveryCost;

    @OneToMany(
            mappedBy = "deliveryMethod",
            fetch = FetchType.LAZY,
            cascade = {
                CascadeType.PERSIST, CascadeType.MERGE,
                CascadeType.DETACH, CascadeType.REFRESH
            })
    List<Order> orderList;
}
