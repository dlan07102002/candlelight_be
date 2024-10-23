package vn.duclan.candlelight_be.model;

import java.util.List;

import lombok.Data;

@Data
public class Wishlist {
    private int wishListId;

    private List<Product> productList;

    private User user;

}
