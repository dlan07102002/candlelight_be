package vn.duclan.candlelight_be.service.custom;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import vn.duclan.candlelight_be.dto.request.OrderStatusRequest;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.exception.ErrorCode;
import vn.duclan.candlelight_be.model.DeliveryMethod;
import vn.duclan.candlelight_be.model.Order;
import vn.duclan.candlelight_be.model.OrderDetail;
import vn.duclan.candlelight_be.model.PaymentMethod;
import vn.duclan.candlelight_be.model.Product;
import vn.duclan.candlelight_be.model.User;
import vn.duclan.candlelight_be.model.enums.PaymentStatus;
import vn.duclan.candlelight_be.repository.DeliveryMethodRepository;
import vn.duclan.candlelight_be.repository.OrderRepository;
import vn.duclan.candlelight_be.repository.PaymentMethodRepository;
import vn.duclan.candlelight_be.repository.ProductRepository;
import vn.duclan.candlelight_be.repository.UserRepository;
import vn.duclan.candlelight_be.service.JwtService;

@Service
@Slf4j
public class OrderService {
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private PaymentMethodRepository paymentMethodRepository;
    private DeliveryMethodRepository deliveryMethodRepository;
    private JwtService jwtService;

    public OrderService(OrderRepository orderRepository,
            UserRepository userRepository,
            PaymentMethodRepository paymentMethodRepository,
            ProductRepository productRepository,
            DeliveryMethodRepository deliveryMethodRepository, JwtService jwtService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.deliveryMethodRepository = deliveryMethodRepository;
        this.productRepository = productRepository;
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

    @Transactional
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

    @Transactional
    public Order pay(OrderStatusRequest request, String token) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order with ID " + request.getOrderId() + " does not exist!"));

        List<OrderDetail> orderDetails = order.getOrderDetailList();

        String username = jwtService.getUsername(token);
        User userGetFromToken = isUserValid(username);
        User userOrderOwner = isUserValid(order.getUsername());
        if (userGetFromToken.getUserId() == userOrderOwner.getUserId()) {

            List<Product> updatedProducts = new ArrayList<>();
            for (OrderDetail od : orderDetails) {
                Product product = productRepository.findById(od.getProductId())
                        .orElseThrow(
                                () -> new RuntimeException("Product with ID " + od.getProductId() + " is not valid"));

                if (product.getQuantity() < od.getQuantity()) {
                    throw new RuntimeException("Not enough stock for product ID: " + od.getProductId());
                }

                product.setQuantity(product.getQuantity() - od.getQuantity());
                updatedProducts.add(product);
            }

            productRepository.saveAll(updatedProducts);

            order.setTotalPrice(request.getTotalPrice());
            order.setPaymentStatus(PaymentStatus.SUCCESS);

            return orderRepository.save(order);
        } else {
            throw new RuntimeException("User is not valid");
        }

    }
}
