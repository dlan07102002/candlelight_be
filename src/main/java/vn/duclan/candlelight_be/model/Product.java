package vn.duclan.candlelight_be.model;

import java.util.List;

import lombok.Data;

@Data
public class Product {
    private int productID;
    private String productName;
    String description;
    double listPrice;
    double sellPrice;
    private int quantity;
    private double rateAverage;

    private List<Category> categoryList;

    private List<Image> images;

    private List<Wishlist> wishlists;

    private List<Review> reviewList;

}
