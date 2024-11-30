package vn.duclan.candlelight_be.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wishlists")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id")
    int wishListId;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {
                CascadeType.PERSIST, CascadeType.MERGE,
                CascadeType.DETACH, CascadeType.REFRESH
            })
    @JoinTable(
            name = "product_wishlist",
            joinColumns = @JoinColumn(name = "wishlist_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    List<Product> productList;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {
                CascadeType.PERSIST, CascadeType.MERGE,
                CascadeType.DETACH, CascadeType.REFRESH
            })
    @JoinColumn(name = "user_id")
    User user;
}
