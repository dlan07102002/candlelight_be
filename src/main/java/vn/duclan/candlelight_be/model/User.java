package vn.duclan.candlelight_be.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Data
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_id")
        int userId;

        @Column(name = "last_name")
        String lastName;

        @Column(name = "first_name")
        String firstName;

        @Column(name = "username")
        @NotEmpty(message = "Username can not be empty")
        String username;

        @Column(name = "password")
        @NotEmpty(message = "Password can not be empty")
        @Size(min = 8, max = 512)
        String password;

        @Enumerated(EnumType.STRING)
        @Column(name = "gender")
        Gender gender;

        @Column(name = "email")
        @Email(message = "Invalid email address.")
        String email;

        @Column(name = "phone_number")
        String phoneNumber;

        @Column(name = "order_address")
        String orderAddress;

        @Column(name = "delivery_address")
        String deliveryAddress;

        // Boolean chấp nhận giá trị null
        @Column(name = "is_activate", columnDefinition = "BOOLEAN")
        Boolean isActivate;

        @Column(name = "activate_code")
        String activateCode;

        // @Column(name = "avatar", columnDefinition = "LONGBLOB")
        // @Lob
        // String avatar;

        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        List<Review> reviewList;

        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        List<Wishlist> wishlists;

        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        List<Order> orderList;

        @ManyToMany(fetch = FetchType.EAGER, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
        List<Role> roleList;

}
