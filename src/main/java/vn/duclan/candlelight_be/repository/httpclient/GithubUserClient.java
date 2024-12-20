package vn.duclan.candlelight_be.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import vn.duclan.candlelight_be.dto.response.outbound.GithubUserResponse;

@FeignClient(name = "github-user-client", url = "https://api.github.com")
public interface GithubUserClient {
    @GetMapping(value = "/user")
    GithubUserResponse getUserInfo(@RequestHeader("Authorization") String bearerToken);

}