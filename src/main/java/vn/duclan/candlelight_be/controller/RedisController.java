package vn.duclan.candlelight_be.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.duclan.candlelight_be.service.BaseRedisService;

@RestController
@RequestMapping("/api/v1/redis")
@RequiredArgsConstructor
public class RedisController {
    private final BaseRedisService redisService;

    @PostMapping
    public void set() {
        redisService.set("hihi", "haha");
    }
}