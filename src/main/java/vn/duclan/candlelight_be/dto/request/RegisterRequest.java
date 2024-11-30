package vn.duclan.candlelight_be.dto.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.duclan.candlelight_be.model.Gender;
import vn.duclan.validator.PasswordConstraints;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {

    String lastName;

    String firstName;

    @NotEmpty(message = "Username can not be empty")
    @Size(min = 3, message = "Username must have at least {min} characters")
    String username;

    @PasswordConstraints(min = 8)
    @NotEmpty(message = "Password can not be empty")
    String password;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Email(message = "Invalid email address.")
    String email;

    String phoneNumber;
    String orderAddress;
    String deliveryAddress;

    @Builder.Default
    Boolean isActivate = false;

    String activateCode;
}
