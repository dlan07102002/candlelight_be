package vn.duclan.candlelight_be.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.duclan.candlelight_be.dto.response.outbound.IpnResponse;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.model.constant.VNPayIPNResponseConst;
import vn.duclan.candlelight_be.model.constant.VNPayParams;
import vn.duclan.candlelight_be.service.custom.OrderService;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IpnHandler {
    final VNPayService vnPayService;
    final OrderService orderService;

    public IpnResponse handle(Map<String, String> params) {
        if (!vnPayService.verifyIpn(params)) {
            return VNPayIPNResponseConst.SIGNATURE_FAILED;
        }

        IpnResponse response;
        String txnRef = params.get(VNPayParams.TXN_REF);

        try {
            Long orderId = Long.parseLong(txnRef);
            orderService.markPaid(orderId);
            response = VNPayIPNResponseConst.SUCCESS;
        } catch (AppException e) {
            // TODO: handle exception

            log.info("Error with payment: {}", e.getMessage());
            response = VNPayIPNResponseConst.ORDER_NOT_FOUND;
        } catch (Exception e) {
            response = VNPayIPNResponseConst.UNKNOWN_ERROR;
        }

        log.info("[VNPay Ipn] txnRef: {}, response: {}", txnRef, response);
        return response;
    }
}
