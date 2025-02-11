package vn.duclan.candlelight_be.controller.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import vn.duclan.candlelight_be.model.OrderDetail;
import vn.duclan.candlelight_be.service.custom.OrderDetailService;

@RestController
@RequestMapping("/api/order-detail")
@Tag(name = "Order Detail Controller")
public class OrderDetailController {
    private OrderDetailService orderDetailService;

    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @PostMapping
    @CrossOrigin(origins = "${fe.host}")
    public ResponseEntity<?> addOderDetail(@RequestBody OrderDetail orderDetail) {
        ResponseEntity<?> response = orderDetailService.save(orderDetail);
        return response;
    }
}
