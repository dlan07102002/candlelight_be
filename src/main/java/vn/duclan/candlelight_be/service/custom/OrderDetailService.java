package vn.duclan.candlelight_be.service.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.duclan.candlelight_be.dao.ImageRepository;
import vn.duclan.candlelight_be.dao.OrderDetailRepository;
import vn.duclan.candlelight_be.dao.OrderRepository;
import vn.duclan.candlelight_be.dao.ProductRepository;
import vn.duclan.candlelight_be.dao.UserRepository;
import vn.duclan.candlelight_be.model.Image;
import vn.duclan.candlelight_be.model.Order;
import vn.duclan.candlelight_be.model.OrderDetail;
import vn.duclan.candlelight_be.model.Product;
import vn.duclan.candlelight_be.model.User;

@Service
public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderDetailService(OrderDetailRepository orderDetailRepository, ProductRepository productRepository,
            ImageRepository imageRepository, UserRepository userRepository, OrderRepository orderRepository) {
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public ResponseEntity<String> save(OrderDetail orderDetail) {
        System.out.println("-----------------------" + orderDetail);
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

        order.setUser(user); // Nếu `order.setUser()` cần thiết
        orderRepository.saveAndFlush(order);

        orderDetail.setProduct(product);

        orderDetail.setOrder(order);

        orderDetailRepository.save(orderDetail);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("OrderDetail saved successfully with ID: " + orderDetail.getOrderDetailId());
    }

}
