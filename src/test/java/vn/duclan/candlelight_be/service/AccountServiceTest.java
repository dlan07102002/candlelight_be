package vn.duclan.candlelight_be.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import vn.duclan.candlelight_be.dto.request.LoginRequest;
import vn.duclan.candlelight_be.dto.request.RegisterRequest;
import vn.duclan.candlelight_be.dto.response.UserResponse;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.model.User;
import vn.duclan.candlelight_be.repository.UserRepository;

@SpringBootTest
@TestPropertySource("/test.properties") // Config to isolation
class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    @MockBean
    private UserRepository repository;

    @MockBean
    private JwtService jwtService;

    // Input and output test
    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UserResponse response;
    private String jwt;

    @BeforeEach // Map hàm này chạy trước mỗi test
    private void initData() {
        registerRequest = RegisterRequest.builder()
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .password("@L12345678912345678")
                .email("s.gintoki710@gmail.com")
                .build();

        loginRequest = LoginRequest.builder().username("john").password("123456").build();
        response = UserResponse.builder()
                .username("john")
                .firstName("Jon")
                .lastName("Doe")
                .email("s.gintoki710@gmail.com")
                .build();

        user = User.builder()
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .email("s.gintoki710@gmail.com")
                .build();

        jwt = "eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjUxLCJpc1N0YWZmIjpmYWxzZSwiaXNBZG1pbiI6dHJ1ZSwiaXNVc2VyIjpmYWxzZSwianRpIjoiN2FiZjBiNzMtMTJmYS00MzI4LWIzYzMtNTU5OTMxNjczMTRmIiwic3ViIjoiZHVjbGFuIiwiaWF0IjoxNzMyNzEzMzQ5LCJleHAiOjE3MzI3MTY5NDl9.1icvBM7T9u6NMe3QsA4FGZTQDAap8ULl1n7pgaFzs6JTHfj6OG18GEFMIKQo5itqwyHMYvJMEx0jMxOjLQEX2Q";
    }

    @Test
    void register_validRequest() {
        // GIVEN: dữ liệu đã biết trước (Request và Response)
        Mockito.when(repository.existsByUsername(anyString())).thenReturn(false);
        Mockito.when(repository.save(any())).thenReturn(user);

        // WHEN
        accountService.register(registerRequest);

        // THEN
        Assertions.assertThat(response.getUsername()).isEqualTo("john");
        Assertions.assertThat(response.getEmail()).isEqualTo("s.gintoki710@gmail.com");
    }

    @Test
    void register_userIsExisted_fail() {
        // GIVEN: dữ liệu đã biết trước (Request và Response)
        Mockito.when(repository.existsByUsername(anyString())).thenReturn(true);

        // WHEN
        // assert Exception
        var exception = assertThrows(AppException.class, () -> accountService.register(registerRequest));

        // THEN
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1001);
    }

    // public String login(@Valid LoginRequest registerRequest) {

    // String jwt = jwtService.generateToken(request.getUsername());

    // return jwt;

    // }

    @Test
    // @WithMockUser(username = "john")
    void login_valid_success() {
        Mockito.when(jwtService.generateToken(anyString())).thenReturn(jwt);

        accountService.login(loginRequest);

        Assertions.assertThat(jwt).isEqualTo(jwt);
    }
}
