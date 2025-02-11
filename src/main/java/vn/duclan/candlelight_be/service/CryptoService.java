package vn.duclan.candlelight_be.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.exception.ErrorCode;
import vn.duclan.candlelight_be.util.EncodingUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class CryptoService {

    private final Mac mac = Mac.getInstance("HmacSHA512");

    @Value("${payment.vnpay.secret-key}")
    private String secretKey;

    public CryptoService() throws NoSuchAlgorithmException {
    }

    @PostConstruct
    void init() throws InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA512");
        mac.init(secretKeySpec);
    }

    public String sign(String data) {
        try {
            return EncodingUtil.toHexString(mac.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
    }
}