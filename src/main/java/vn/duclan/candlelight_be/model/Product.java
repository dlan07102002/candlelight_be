package vn.duclan.candlelight_be.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "products")
public class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "product_id")
        private int productId;

        @Column(name = "product_name")
        private String productName;

        @Column(name = "description", columnDefinition = "text")
        private String description;

        @Column(name = "list_price")
        private double listPrice;

        @Column(name = "sell_price")
        private double sellPrice;

        @Column(name = "quantity")
        private int quantity;

        @Column(name = "rate_average")
        private double rateAverage;

        // @Column(name = "brand")
        // private String brand;

        // @Column(name = "weight")
        // private double weight;

        @ManyToMany(
                        // fetch = FetchType.LAZY, cascade = {
                        // CascadeType.PERSIST, CascadeType.MERGE,
                        // CascadeType.DETACH, CascadeType.REFRESH
                        // }
                        mappedBy = "productList")
        // FK of product_category point to Product entity by @JoinColumn(name =
        // "product_id")
        // @JoinTable(name = "product_category", joinColumns = @JoinColumn(name =
        // "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
        private List<Category> categoryList;

        @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        private List<Image> images;

        @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        private List<Review> reviewList;

        @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        private List<OrderDetail> orderDetailList;

        @ManyToMany(
                        // fetch = FetchType.LAZY, cascade = {
                        // CascadeType.PERSIST, CascadeType.MERGE,
                        // CascadeType.DETACH, CascadeType.REFRESH
                        // }
                        mappedBy = "productList")
        // @JoinTable(name = "product_wishlist", joinColumns = @JoinColumn(name =
        // "product_id"), inverseJoinColumns = @JoinColumn(name = "wishlist_id"))
        private List<Wishlist> wishlists;

}
