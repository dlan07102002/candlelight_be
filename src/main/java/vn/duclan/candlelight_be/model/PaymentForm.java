package vn.duclan.candlelight_be.model;

import java.util.List;

import lombok.Data;

@Data
public class PaymentForm {
    private int paymentFormId;
    private String paymentFormName;
    private double paymentCost;
    private double description;

    private List<Order> orderList;
}
