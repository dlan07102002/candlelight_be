package vn.duclan.candlelight_be.controller.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.model.Order;
import vn.duclan.candlelight_be.service.custom.OrderService;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController {
    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<?> createOrder(@RequestBody Order order,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = "";
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7); // Loại bỏ "Bearer " để lấy token
            }

            return ResponseEntity.ok(orderService.save(order, token));

        } catch (AppException e) {
            // TODO: handle exception

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
