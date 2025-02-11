package vn.duclan.candlelight_be.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import vn.duclan.candlelight_be.dto.request.PayRequest;
import vn.duclan.candlelight_be.dto.response.PayResponse;
import vn.duclan.candlelight_be.model.Order;
import vn.duclan.candlelight_be.model.constant.VNPayParams;
import vn.duclan.candlelight_be.model.enums.Currency;
import vn.duclan.candlelight_be.model.enums.Locale;
import vn.duclan.candlelight_be.repository.OrderRepository;
import vn.duclan.candlelight_be.util.DateUtil;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class VNPayService {
    final OrderRepository orderRepository;
    final CryptoService cryptoService;

    public static final String VERSION = "2.1.0";
    public static final String COMMAND = "pay";
    public static final String ORDER_TYPE = "190000";
    public static final long DEFAULT_MULTIPLIER = 100L;

    @Value("${payment.vnpay.return-url}")
    @NonFinal
    String returnUrlFormat;

    @Value("${payment.vnpay.init-payment-url}")
    @NonFinal
    String vnpayUrl;

    @Value("${payment.vnpay.timeout}")
    @NonFinal
    int paymentTimeout;

    @Value("${payment.vnpay.tmn-code}")
    @NonFinal
    String tmnCode;

    @Transactional
    public PayResponse vnpay(PayRequest request) {
        log.info("Order: {}", request);
        try {
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order with ID " +
                            request.getOrderId() + " does not exist!"));

            var amount = (long) request.getTotalPrice() * DEFAULT_MULTIPLIER;
            var txnRef = request.getOrderId();
            var returnUrl = returnUrl(txnRef + "");
            var vnCalendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            var createdDate = DateUtil.formatVnTime(vnCalendar);
            vnCalendar.add(Calendar.MINUTE, paymentTimeout);
            var expiredDate = DateUtil.formatVnTime(vnCalendar);

            var ipAddress = request.getIpAddress();
            var orderInfo = orderInfo(txnRef + "");
            // var requestId

            Map<String, String> params = new HashMap<>();

            params.put(VNPayParams.VERSION, VERSION);
            params.put(VNPayParams.COMMAND, COMMAND);

            params.put(VNPayParams.TMN_CODE, tmnCode);
            params.put(VNPayParams.AMOUNT, String.valueOf(amount));
            params.put(VNPayParams.CURRENCY, Currency.VND.getValue());

            params.put(VNPayParams.TXN_REF, txnRef + "");
            params.put(VNPayParams.RETURN_URL, returnUrl);

            params.put(VNPayParams.CREATED_DATE, createdDate);
            params.put(VNPayParams.EXPIRE_DATE, expiredDate);

            params.put(VNPayParams.IP_ADDRESS, ipAddress);
            params.put(VNPayParams.LOCALE, Locale.VIETNAM.getCode());

            params.put(VNPayParams.ORDER_INFO, orderInfo);
            params.put(VNPayParams.ORDER_TYPE, ORDER_TYPE);

            return PayResponse.builder().order(order).vnpUrl(initPaymentUrl(params)).build();
        } catch (Exception e) {
            log.info("Error: {}", e.getMessage());
            return PayResponse.builder().vnpUrl(e.getMessage()).build();
        }
    }

    public boolean verifyIpn(Map<String, String> params) {
        var reqSecureHash = params.get(VNPayParams.SECURE_HASH);
        params.remove(VNPayParams.SECURE_HASH);
        params.remove(VNPayParams.SECURE_HASH_TYPE);
        var hashPayload = new StringBuilder();
        var fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        var itr = fieldNames.iterator();
        while (itr.hasNext()) {
            var fieldName = itr.next();
            var fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // Build hash data
                hashPayload.append(fieldName);
                hashPayload.append("=");
                hashPayload.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    hashPayload.append("&");
                }
            }
        }

        var secureHash = cryptoService.sign(hashPayload.toString());
        return secureHash.equals(reqSecureHash);
    }

    @SneakyThrows
    String initPaymentUrl(Map<String, String> params) {
        var hashPayload = new StringBuilder();
        var query = new StringBuilder();
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        var itr = fieldNames.iterator();
        while (itr.hasNext()) {
            var fieldName = itr.next();
            var fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // 2.1. Build hash data
                hashPayload.append(fieldName);
                hashPayload.append("=");
                hashPayload.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                // 2.2. Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append("=");
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    query.append("&");
                    hashPayload.append("&");
                }
            }
        }
        // Build secureHash
        String secureHash = cryptoService.sign(hashPayload.toString());

        query.append("&vnp_SecureHash=");
        query.append(secureHash);

        return vnpayUrl + "?" + query;
    }

    String returnUrl(String txnRef) {

        return String.format(returnUrlFormat, txnRef);
    }

    String orderInfo(String txnRef) {
        return String.format("Thanh toan order: %s", txnRef);
    }

}
