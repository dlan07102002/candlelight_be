
package vn.duclan.candlelight_be.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import feign.QueryMap;
import vn.duclan.candlelight_be.dto.request.outbound.ExchangeTokenRequest;
import vn.duclan.candlelight_be.dto.response.outbound.ExchangeTokenResponse;

@FeignClient(name = "google-identity", url = "https://oauth2.googleapis.com")
public interface GoogleIdentityClient {
    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
    ExchangeTokenResponse exchangeToken(
            @QueryMap ExchangeTokenRequest request);
}
