package vn.duclan.candlelight_be.model;

import lombok.Data;

@Data
public class OrderDetail {
    private int orderDetailId;

    private int quantity;

    private double sellPrice;

    private Product product;

    private Order order;

}
