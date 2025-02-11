package vn.duclan.candlelight_be.controller.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import vn.duclan.candlelight_be.dto.request.OrderRequest;
import vn.duclan.candlelight_be.dto.request.PayRequest;
import vn.duclan.candlelight_be.dto.response.APIResponse;
import vn.duclan.candlelight_be.dto.response.OrderStatusResponse;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.exception.ErrorCode;
import vn.duclan.candlelight_be.model.Order;
import vn.duclan.candlelight_be.service.VNPayService;
import vn.duclan.candlelight_be.service.custom.OrderService;
import vn.duclan.candlelight_be.util.RequestUtil;

@RestController
@RequestMapping("/api/order")
@Slf4j
@Tag(name = "Order Controller")
public class OrderController {
    private OrderService orderService;
    private VNPayService payService;

    OrderController(OrderService orderService, VNPayService payService) {
        this.orderService = orderService;
        this.payService = payService;
    }

    @GetMapping("")
    @CrossOrigin(origins = "${fe.host}")
    Double calculateRevenue() {
        return orderService.calculateRevenue();
    }

    @PostMapping("")
    @CrossOrigin(origins = "${fe.host}")
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
    APIResponse<?> updateStatus(@RequestBody OrderRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = "";
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7); // Loại bỏ "Bearer " để lấy token
            }
            request.setToken(token);

            return APIResponse.builder().result(orderService.update(request)).build();

        } catch (AppException e) {
            // TODO: handle exception

            return APIResponse.builder().code(ErrorCode.BAD_REQUEST.getCode()).build();
        }
    }

    // Post or Patch.......
    @PostMapping("/pay/vnpay")
    APIResponse<?> vnpay(@RequestBody PayRequest request,
            @RequestHeader("Authorization") String authorizationHeader, HttpServletRequest httpServletRequest) {
        try {
            String token = "";
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
            }

            var ipAdress = RequestUtil.getIpAddr(httpServletRequest);
            request.setIpAddress(ipAdress);
            request.setToken(token);
            return APIResponse.builder().result(payService.vnpay(request)).build();

        } catch (AppException e) {
            // TODO: handle exception

            return APIResponse.builder().code(ErrorCode.BAD_REQUEST.getCode()).build();
        }
    }

    @GetMapping("/{orderId}/status")
    APIResponse<OrderStatusResponse> getOrderStatus(@PathVariable Long orderId) {
        return APIResponse.<OrderStatusResponse>builder().result(orderService.getOrderStatus(orderId)).build();
    }
}
