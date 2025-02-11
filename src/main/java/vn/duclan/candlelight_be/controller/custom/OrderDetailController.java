package vn.duclan.candlelight_be.controller.custom;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.duclan.candlelight_be.model.OrderDetail;
import vn.duclan.candlelight_be.service.custom.OrderDetailService;

@RestController
@RequestMapping("/api/order-detail")
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
