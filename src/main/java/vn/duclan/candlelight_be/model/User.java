package vn.duclan.candlelight_be.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.duclan.candlelight_be.model.enums.Gender;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
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

        // COLLATE utf8mb4_unicode_ci: pb upper and lower case
        @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
        // @NotEmpty(message = "Username can not be empty")
        String username;

        @Column(name = "password")
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password;

        @Enumerated(EnumType.STRING)
        @Column(name = "gender")
        Gender gender;

        @Column(name = "email", unique = true)
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
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String activateCode;

        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        @Builder.Default
        List<Review> reviewList = new ArrayList<>();

        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @Builder.Default
        List<Wishlist> wishlists = new ArrayList<>();

        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @Builder.Default
        List<Order> orderList = new ArrayList<>();

        @ManyToMany(fetch = FetchType.EAGER, cascade = {
                        CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH
        })
        @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
        @Builder.Default
        List<Role> roleList = new ArrayList<>();
}
