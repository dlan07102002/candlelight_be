package vn.duclan.candlelight_be.model;

import java.util.List;

import lombok.Data;

@Data
public class User {
    private String userId;
    private String lastName;
    private String firstName;
    private String username;
    private String password;
    private boolean gender;
    private String email;
    private String phoneNumber;
    private String orderAddress;
    private String deliveryAddress;

    private List<Role> roleList;

    private List<Order> orderList;

    private List<Wishlist> wishlists;

    private List<Review> reviewList;
}
