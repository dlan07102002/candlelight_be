package vn.duclan.candlelight_be.controller.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import vn.duclan.candlelight_be.dto.request.OrderStatusRequest;
import vn.duclan.candlelight_be.dto.response.APIResponse;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.exception.ErrorCode;
import vn.duclan.candlelight_be.model.Order;
import vn.duclan.candlelight_be.service.custom.OrderService;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController {
    private OrderService orderService;

    OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("")
    @CrossOrigin(origins = "http://localhost:5173")
    Double calculateRevenue() {
        return orderService.calculateRevenue();
    }

    @PostMapping("")
    @CrossOrigin(origins = "http://localhost:5173")
    ResponseEntity<?> createOrder(@RequestBody Order order,
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

    @PatchMapping
    APIResponse<?> updateStatus(@RequestBody OrderStatusRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = "";
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7); // Loại bỏ "Bearer " để lấy token
            }

            return APIResponse.builder().result(orderService.update(request, token)).build();

        } catch (AppException e) {
            // TODO: handle exception

            return APIResponse.builder().code(ErrorCode.BAD_REQUEST.getCode()).build();
        }
    }

    @PatchMapping("/pay")
    APIResponse<?> pay(@RequestBody OrderStatusRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = "";
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
            }

            return APIResponse.builder().result(orderService.pay(request, token)).build();

        } catch (AppException e) {
            // TODO: handle exception

            return APIResponse.builder().code(ErrorCode.BAD_REQUEST.getCode()).build();
        }
    }
}
