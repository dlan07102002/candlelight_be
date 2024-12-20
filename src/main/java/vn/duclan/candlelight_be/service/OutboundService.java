package vn.duclan.candlelight_be.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import vn.duclan.candlelight_be.dto.request.outbound.ExchangeTokenRequest;
import vn.duclan.candlelight_be.dto.response.outbound.ExchangeTokenResponse;
import vn.duclan.candlelight_be.dto.response.outbound.GithubUserResponse;
import vn.duclan.candlelight_be.dto.response.outbound.GoogleUserResponse;
import vn.duclan.candlelight_be.model.Role;
import vn.duclan.candlelight_be.model.User;
import vn.duclan.candlelight_be.repository.RoleRepository;
import vn.duclan.candlelight_be.repository.UserRepository;
import vn.duclan.candlelight_be.repository.httpclient.GithubIdentityClient;
import vn.duclan.candlelight_be.repository.httpclient.GithubUserClient;
import vn.duclan.candlelight_be.repository.httpclient.GoogleIdentityClient;
import vn.duclan.candlelight_be.repository.httpclient.GoogleUserClient;

public interface OutboundService {

    public Object exchangeToken(String clientId, String clientSecret, String code, String redirectUri);

    public Object getUserInfo(String token);

}

@Component
@Slf4j
class GithubService implements OutboundService {
    private GithubIdentityClient githubIdentityClient;
    private GithubUserClient githubUserClient;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public GithubService(GithubIdentityClient githubIdentityClient, GithubUserClient githubUserClient,
            UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.githubIdentityClient = githubIdentityClient;
        this.githubUserClient = githubUserClient;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ExchangeTokenResponse exchangeToken(String clientId, String clentSecret, String code, String redirectUri) {
        return githubIdentityClient.exchangeToken(clientId, clentSecret, code, redirectUri);
    }

    @Override
    public GithubUserResponse getUserInfo(String token) {
        String bearerToken = "Bearer " + token;
        log.info(bearerToken);
        var userInfo = githubUserClient.getUserInfo(bearerToken);

        // User Onboard
        userRepository.findByUsername(userInfo.getLogin()).orElseGet(
                () -> {

                    List<Role> roleList = new ArrayList<>();
                    roleList.add(roleRepository.findByRoleName("USER"));
                    return userRepository
                            .save(User.builder().username(userInfo.getLogin())
                                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                                    .firstName(userInfo.getName())
                                    .isActivate(true).roleList(roleList).build());
                });
        return userInfo;
    }

}

@Component
class GoogleService implements OutboundService {
    private GoogleIdentityClient googleIdentityClient;
    private GoogleUserClient googleUserClient;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public GoogleService(GoogleIdentityClient googleIdentityClient, GoogleUserClient googleUserClient,
            UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.googleIdentityClient = googleIdentityClient;
        this.googleUserClient = googleUserClient;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ExchangeTokenResponse exchangeToken(String clientId, String clientSecret, String code, String redirectUri) {
        return googleIdentityClient.exchangeToken(
                ExchangeTokenRequest.builder().code(code).clientId(clientId).clientSecret(clientSecret)
                        .redirectUri(redirectUri).grantType("authorization_code").build());
    }

    @Override
    public GoogleUserResponse getUserInfo(String token) {

        var userInfo = googleUserClient.getUserInfo("json", token);

        // User Onboard
        userRepository.findByUsername(userInfo.getEmail()).orElseGet(
                () -> {

                    List<Role> roleList = new ArrayList<>();
                    roleList.add(roleRepository.findByRoleName("USER"));
                    return userRepository
                            .save(User.builder().username(userInfo.getEmail())
                                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                                    .firstName(userInfo.getGivenName())
                                    .lastName(userInfo.getFamilyName()).isActivate(true).roleList(roleList).build());
                });
        return userInfo;
    }

}
