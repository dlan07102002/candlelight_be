package vn.duclan.candlelight_be.service.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.duclan.candlelight_be.model.Order;
import vn.duclan.candlelight_be.model.OrderDetail;
import vn.duclan.candlelight_be.model.Product;
import vn.duclan.candlelight_be.model.User;
import vn.duclan.candlelight_be.repository.ImageRepository;
import vn.duclan.candlelight_be.repository.OrderDetailRepository;
import vn.duclan.candlelight_be.repository.OrderRepository;
import vn.duclan.candlelight_be.repository.ProductRepository;
import vn.duclan.candlelight_be.repository.UserRepository;

@Service
public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderDetailService(
            OrderDetailRepository orderDetailRepository,
            ProductRepository productRepository,
            ImageRepository imageRepository,
            UserRepository userRepository,
            OrderRepository orderRepository) {
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public ResponseEntity<String> save(OrderDetail orderDetail) {
        Product product = productRepository.findById(orderDetail.getProductId()).get();
        User user = userRepository.findById(orderDetail.getUserId()).get(); // Assuming you meant getUserId()
        Order order = orderRepository.findById(orderDetail.getOrderId()).get();

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found with id: " + orderDetail.getProductId());
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with id: " + orderDetail.getUserId());
        }

        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Order not found with id: " + orderDetail.getOrderId());
        }

        orderDetail.setProduct(product);

        orderDetail.setOrder(order);

        orderDetailRepository.save(orderDetail);

        order.setUser(user); // Nếu `order.setUser()` cần thiết
        order.calculateTotalPrice();
        orderRepository.saveAndFlush(order);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderDetail.getOrderDetailId() + "");
    }
}
