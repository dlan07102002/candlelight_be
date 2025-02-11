package vn.duclan.candlelight_be.dto.response;

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
public class OrderStatusResponse {
    Long orderId;
    Long userId;
    DeliveryStatus deliveryStatus;
    PaymentStatus paymentStatus;

}
