package vn.duclan.candlelight_be.dto.request;

import jakarta.persistence.Column;
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
import vn.duclan.candlelight_be.model.enums.DeliveryStatus;
import vn.duclan.candlelight_be.model.enums.PaymentStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatusRequest {
    long orderId;

    @Enumerated(EnumType.STRING)
    DeliveryStatus deliveryStatus;

    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;
}
