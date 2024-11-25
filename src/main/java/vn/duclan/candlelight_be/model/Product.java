package vn.duclan.candlelight_be.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Table(name = "products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "product_id")
        int productId;

        @Column(name = "product_name")
        String productName;

        @Column(name = "description")
        String description;

        @Column(name = "detail_description", columnDefinition = "MEDIUMTEXT")
        String detailDescription;

        @Column(name = "list_price")
        Double listPrice;

        @Column(name = "sell_price")
        Double sellPrice;

        @Column(name = "quantity")
        Integer quantity;

        @Column(name = "rate_average")
        Double rateAverage;

        // @Column(name = "brand")
        // String brand;

        // @Column(name = "weight")
        // double weight;

        @ManyToMany(fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        }, mappedBy = "productList")
        // FK of product_category point to Product entity by @JoinColumn(name =
        // "product_id")
        // @JoinTable(name = "product_category", joinColumns = @JoinColumn(name =
        // "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
        List<Category> categoryList;

        @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        List<Image> images;

        @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        List<Review> reviewList;

        @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        List<OrderDetail> orderDetailList;

        @ManyToMany(
                        // fetch = FetchType.LAZY, cascade = {
                        // CascadeType.PERSIST, CascadeType.MERGE,
                        // CascadeType.DETACH, CascadeType.REFRESH
                        // }
                        mappedBy = "productList")
        // @JoinTable(name = "product_wishlist", joinColumns = @JoinColumn(name =
        // "product_id"), inverseJoinColumns = @JoinColumn(name = "wishlist_id"))
        List<Wishlist> wishlists;

}
