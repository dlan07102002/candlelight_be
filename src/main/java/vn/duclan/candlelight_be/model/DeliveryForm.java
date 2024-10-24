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
import lombok.Data;

@Data
@Entity
@Table(name = "delivery_forms")
public class DeliveryForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_form_id")
    private int deliveryFormId;

    @Column(name = "delivery_form_name")
    private String deliveryFormName;

    @Column(name = "description")
    private String description;

    @Column(name = "delivery_cost")
    private double deliveryCost;

    @OneToMany(mappedBy = "deliveryForm", fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    List<Order> orderList;
}
