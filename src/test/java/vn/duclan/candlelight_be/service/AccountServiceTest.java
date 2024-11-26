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

import vn.duclan.candlelight_be.dto.request.RegisterRequest;
import vn.duclan.candlelight_be.dto.response.UserResponse;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.model.User;
import vn.duclan.candlelight_be.repository.UserRepository;

@SpringBootTest
public class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    @MockBean
    private UserRepository repository;

    // Input and output test
    private User user;
    private RegisterRequest request;
    private UserResponse response;

    @BeforeEach // Map hàm này chạy trước mỗi test
    private void initData() {
        request = RegisterRequest.builder().username("john")
                .firstName("John").lastName("Doe").password("@L12345678912345678").email("s.gintoki710@gmail.com")
                .build();
        response = UserResponse.builder().username("john")
                .firstName("Jon").lastName("Doe").email("s.gintoki710@gmail.com").build();

        user = User.builder().username("john")
                .firstName("John").lastName("Doe").email("s.gintoki710@gmail.com")
                .build();

    }

    @Test
    void register_validRequest() {
        // GIVEN: dữ liệu đã biết trước (Request và Response)
        Mockito.when(repository.existsByUsername(anyString())).thenReturn(false);
        Mockito.when(repository.save(any())).thenReturn(user);

        // WHEN
        accountService.register(request);

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
        var exception = assertThrows(AppException.class, () -> accountService.register(request));

        // THEN
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1001);

    }

}
