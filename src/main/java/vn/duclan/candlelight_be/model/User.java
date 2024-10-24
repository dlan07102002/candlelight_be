package vn.duclan.candlelight_be.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_id")
        private int userId;

        @Column(name = "last_name")
        private String lastName;

        @Column(name = "first_name")
        private String firstName;

        @Column(name = "username")
        private String username;

        @Column(name = "password", length = 512)
        private String password;

        @Column(name = "gender")
        private boolean gender;

        @Column(name = "email")
        private String email;

        @Column(name = "phone_number")
        private String phoneNumber;

        @Column(name = "order_address")
        private String orderAddress;

        @Column(name = "delivery_address")
        private String deliveryAddress;

        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        private List<Review> reviewList;

        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        private List<Wishlist> wishlists;

        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        private List<Order> orderList;

        @ManyToMany(cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
        private List<Role> roleList;

}
