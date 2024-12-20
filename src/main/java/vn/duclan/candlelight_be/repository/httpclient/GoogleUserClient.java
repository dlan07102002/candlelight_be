package vn.duclan.candlelight_be.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.duclan.candlelight_be.dto.response.outbound.GoogleUserResponse;

@FeignClient(name = "google-user-client", url = "https://www.googleapis.com")
public interface GoogleUserClient {
    @GetMapping(value = "/oauth2/v1/userinfo")
    GoogleUserResponse getUserInfo(@RequestParam("alt") String alt, @RequestParam("access_token") String accessToken);

}