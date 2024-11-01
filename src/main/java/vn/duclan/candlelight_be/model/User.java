package vn.duclan.candlelight_be.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

enum Gender {
        MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY
}

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
        @NotEmpty(message = "Username can not be empty")
        private String username;

        @Column(name = "password", length = 512)
        @NotEmpty(message = "Password can not be empty")
        private String password;

        @Enumerated(EnumType.STRING)
        @Column(name = "gender")
        private Gender gender;

        @Column(name = "email")
        @Email(message = "Email is not valid")
        private String email;

        @Column(name = "phone_number")
        private String phoneNumber;

        @Column(name = "order_address")
        private String orderAddress;

        @Column(name = "delivery_address")
        private String deliveryAddress;

        @Column(name = "is_activate", columnDefinition = "BOOLEAN")
        private boolean isActivate;

        @Column(name = "activate_code")
        private String activateCode;

        @Column(name = "avatar", columnDefinition = "LONGBLOB")
        @Lob
        private String avatar;

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

        @ManyToMany(fetch = FetchType.EAGER, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
        private List<Role> roleList;

}
