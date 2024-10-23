package vn.duclan.candlelight_be.model;

import java.util.List;

import lombok.Data;

@Data
public class Category {
    private int categoryId;
    private int categoryName;

    List<Product> productList;

}
