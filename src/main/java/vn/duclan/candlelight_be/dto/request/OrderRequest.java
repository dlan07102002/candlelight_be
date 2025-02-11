package vn.duclan.candlelight_be.dto.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

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
public class OrderRequest {
    String token;
    Long orderId;

    @Enumerated(EnumType.STRING)
    DeliveryStatus deliveryStatus;

    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;

    double totalPrice;

}
