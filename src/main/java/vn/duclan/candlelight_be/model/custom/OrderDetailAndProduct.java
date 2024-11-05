package vn.duclan.candlelight_be.model.custom;

import vn.duclan.candlelight_be.model.OrderDetail;
import vn.duclan.candlelight_be.model.Product;

public class OrderDetailAndProduct {
    private OrderDetail orderDetail;
    private Product product;

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    public Product getProduct() {
        return product;
    }

    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public OrderDetailAndProduct(OrderDetail orderDetail, Product product) {
        this.orderDetail = orderDetail;
        this.product = product;
    }

}
