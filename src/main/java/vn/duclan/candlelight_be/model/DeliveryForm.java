package vn.duclan.candlelight_be.model;

import java.util.List;

import lombok.Data;

@Data
public class DeliveryForm {
    private int deliveryFormId;
    private String deliveryFormName;
    private String description;
    private double deliveryCost;
    List<Order> orderList;
}
