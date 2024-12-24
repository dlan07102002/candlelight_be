package vn.duclan.candlelight_be.service.custom;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import vn.duclan.candlelight_be.dto.request.OrderStatusRequest;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.exception.ErrorCode;
import vn.duclan.candlelight_be.model.DeliveryMethod;
import vn.duclan.candlelight_be.model.Order;
import vn.duclan.candlelight_be.model.PaymentMethod;
import vn.duclan.candlelight_be.model.User;
import vn.duclan.candlelight_be.model.enums.PaymentStatus;
import vn.duclan.candlelight_be.repository.DeliveryMethodRepository;
import vn.duclan.candlelight_be.repository.OrderRepository;
import vn.duclan.candlelight_be.repository.PaymentMethodRepository;
import vn.duclan.candlelight_be.repository.UserRepository;
import vn.duclan.candlelight_be.service.JwtService;

@Service
@Slf4j
public class OrderService {
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private PaymentMethodRepository paymentMethodRepository;
    private DeliveryMethodRepository deliveryMethodRepository;
    private JwtService jwtService;

    public OrderService(OrderRepository orderRepository,
            UserRepository userRepository,
            PaymentMethodRepository paymentMethodRepository,
            DeliveryMethodRepository deliveryMethodRepository, JwtService jwtService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.deliveryMethodRepository = deliveryMethodRepository;
        this.jwtService = jwtService;
    }

    public long save(Order order, String token) {
        log.info("Saving order for user ID: {}", order.getUserId());
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

        Order savedOrder = orderRepository.save(order);

        return savedOrder.getOrderId();
    }

    public long update(OrderStatusRequest request, String token) {
        log.info("Updating order...");
        String username = jwtService.getUsername(token);
        User user = isUserValid(username);
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

    private User isUserValid(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    return new AppException(ErrorCode.UNAUTHENTICATION);
                });
    }

    public Double calculateRevenue() {
        // Must chage to SUCCESS, Pending here to test
        return orderRepository.calculateRevenue(PaymentStatus.PENDING);
    }

}
