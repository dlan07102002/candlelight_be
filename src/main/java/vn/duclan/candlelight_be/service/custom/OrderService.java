package vn.duclan.candlelight_be.service.custom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

import vn.duclan.candlelight_be.dto.request.OrderRequest;
import vn.duclan.candlelight_be.dto.response.OrderStatusResponse;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.exception.ErrorCode;
import vn.duclan.candlelight_be.model.DeliveryMethod;
import vn.duclan.candlelight_be.model.Order;
import vn.duclan.candlelight_be.model.PaymentMethod;
import vn.duclan.candlelight_be.model.User;
import vn.duclan.candlelight_be.model.enums.DeliveryStatus;
import vn.duclan.candlelight_be.model.enums.PaymentStatus;
import vn.duclan.candlelight_be.repository.DeliveryMethodRepository;
import vn.duclan.candlelight_be.repository.OrderRepository;
import vn.duclan.candlelight_be.repository.PaymentMethodRepository;
import vn.duclan.candlelight_be.repository.UserRepository;
import vn.duclan.candlelight_be.service.JwtService;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OrderService {
    final OrderRepository orderRepository;
    final UserRepository userRepository;
    final PaymentMethodRepository paymentMethodRepository;
    final DeliveryMethodRepository deliveryMethodRepository;
    final JwtService jwtService;

    @Value("${payment.vnpay.return-url}")
    @NonFinal
    String returnUrlFormat;

    @Value("${payment.vnpay.init-payment-url}")
    @NonFinal
    String vnpayUrl;

    @Value("${payment.vnpay.timeout}")
    @NonFinal
    long paymentTimeout;

    @Value("${payment.vnpay.secret-key}")
    @NonFinal
    String secretKey;

    @Value("${payment.vnpay.tmn-code}")
    @NonFinal
    String tmnCode;

    public long save(Order order, String token) {

        String username = jwtService.getUsername(token);
        User user = isUserValid(username);
        order.setUser(user);

        if (order.getPaymentMethodId() != 0) {
            PaymentMethod paymentMethod = paymentMethodRepository.findById(order.getPaymentMethodId())
                    .orElseThrow(() -> new AppException(ErrorCode.BAD_REQUEST));

            order.setPaymentMethod(paymentMethod);
        }

        if (order.getDeliveryMethodId() != 0) {
            DeliveryMethod deliveryMethod = deliveryMethodRepository.findById(order.getDeliveryMethodId())
                    .orElseThrow(() -> new AppException(ErrorCode.BAD_REQUEST));

            order.setDeliveryMethod(deliveryMethod);
        }

        Order latestOrder = new Order();
        try {
            latestOrder = orderRepository.findTopByUser_UserIdOrderByOrderIdDesc(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("Not found"));

            if (!latestOrder.getDeliveryStatus().equals(DeliveryStatus.PENDING)) {
                throw new RuntimeException("Order is completed");
            }

        } catch (Exception e) {
            // TODO: handle exception

            Order savedOrder = orderRepository.save(order);
            return savedOrder.getOrderId();
        }
        return latestOrder.getOrderId();
    }

    @Transactional
    public Long update(OrderRequest request) {
        log.info("Updating order...");
        String username = jwtService.getUsername(request.getToken());
        isUserValid(username);
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order is not exist"));
        // Update delivery status
        if (request.getDeliveryStatus() != null) {
            order.setDeliveryStatus(request.getDeliveryStatus());
        }
        if (request.getPaymentStatus() != null) {
            order.setPaymentStatus(request.getPaymentStatus());
        }

        orderRepository.saveAndFlush(order);

        return order.getOrderId();
    }

    User isUserValid(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    return new AppException(ErrorCode.UNAUTHENTICATION);
                });
    }

    public Double calculateRevenue() {
        // Must chage to SUCCESS, Pending here to test
        return orderRepository.calculateRevenue(PaymentStatus.PENDING);
    }

    @Transactional
    public void markPaid(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            order.setPaymentStatus(PaymentStatus.SUCCESS);
            order.setDeliveryStatus(DeliveryStatus.DELIVERED);
            orderRepository.saveAndFlush(order);

        } catch (Exception e) {
            // TODO: handle exception
            log.info("Order payment failed: {}", e.getMessage());
            throw new AppException(ErrorCode.PAYMENT_ERROR);
        }

    }

    public OrderStatusResponse getOrderStatus(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return OrderStatusResponse.builder().orderId(orderId).userId(order.getUserId())
                .deliveryStatus(order.getDeliveryStatus())
                .paymentStatus(order.getPaymentStatus()).build();
    }

}
