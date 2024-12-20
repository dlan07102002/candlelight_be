
package vn.duclan.candlelight_be.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.duclan.candlelight_be.dto.response.outbound.ExchangeTokenResponse;

//After
@FeignClient(name = "github-identity", url = "https://github.com/login/oauth")
public interface GithubIdentityClient {
    @PostMapping(value = "/access_token", consumes = "application/x-www-form-urlencoded", headers = "Accept=application/json")
    ExchangeTokenResponse exchangeToken(@RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("code") String code,
            @RequestParam("redirect_uri") String redirectUri);
}
