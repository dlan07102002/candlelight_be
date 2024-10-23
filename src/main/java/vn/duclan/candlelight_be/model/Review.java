package vn.duclan.candlelight_be.model;

import lombok.Data;

@Data
public class Review {
    private long reviewId;
    private float rate;
    private String comment;

    private User user;

    private Product product;
}
