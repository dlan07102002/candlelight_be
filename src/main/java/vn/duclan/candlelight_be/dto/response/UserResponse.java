package vn.duclan.candlelight_be.dto.response;

import org.hibernate.validator.constraints.UniqueElements;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.duclan.candlelight_be.model.Gender;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    String lastName;

    String firstName;

    @UniqueElements
    String username;

    @Enumerated(EnumType.STRING)
    Gender gender;

    String email;

    String phoneNumber;
    String orderAddress;
    String deliveryAddress;
    Boolean isActivate;

}
