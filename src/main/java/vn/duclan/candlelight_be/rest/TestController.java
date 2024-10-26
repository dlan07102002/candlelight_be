package vn.duclan.candlelight_be.rest;

import org.springframework.web.bind.annotation.RestController;

import vn.duclan.candlelight_be.dao.OrderDetailRepository;
import vn.duclan.candlelight_be.model.OrderDetail;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class TestController {
    private OrderDetailRepository repository;

    public TestController(OrderDetailRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/od")
    public OrderDetail test() {
        return repository.findById((long) 1).get();
    }

}
