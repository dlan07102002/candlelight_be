package vn.duclan.candlelight_be.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.duclan.candlelight_be.service.IpnHandler;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
// API for VNPAY call
public class PaymentController {
    private final IpnHandler IpnHandler;

    @GetMapping("/vnpay_ipn")
    Object handleIpn(@RequestParam Map<String, String> params) {
        log.info("VNPay IPN: {}", params);
        return IpnHandler.handle(params);
    }
}
