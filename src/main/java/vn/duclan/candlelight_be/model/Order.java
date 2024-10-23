package vn.duclan.candlelight_be.model;

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class Order {
    private int orderId;
    private Date createdAt;
    private String orderAddress;
    private String deliveryAddress;
    private double deliveryPrice;
    private double totalPrice;

    private User user;

    private List<OrderDetail> orderDetailList;

    private String paymentStatus;

    private String deliveryStatus;

    private PaymentForm paymentForm;

    private DeliveryForm deliveryForm;
}
