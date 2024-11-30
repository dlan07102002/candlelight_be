package vn.duclan.candlelight_be.controller;

import static org.mockito.ArgumentMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import vn.duclan.candlelight_be.dto.request.RegisterRequest;
import vn.duclan.candlelight_be.dto.response.UserResponse;
import vn.duclan.candlelight_be.service.AccountService;

@Slf4j
// Create logger avoid boilerplate code
@SpringBootTest // Khởi động toàn bộ ngữ cảnh Spring (ApplicationContext) để test
// tích hợp.
// helps to load the entire application context
@AutoConfigureMockMvc // Create Mock request to Controller
@TestPropertySource("/test.properties") // Config to isolation
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean // Tạo ra một bean giả lập (mock) trong ngữ cảnh Spring
    private AccountService accountService;

    // Input and output test
    private RegisterRequest request;
    private UserResponse response;

    @BeforeEach // Chỉ định một phương thức sẽ được chạy trước mỗi test case.
    private void initData() {
        request = RegisterRequest.builder()
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .password("@L12345678912345678")
                .email("s.gintoki710@gmail.com")
                .build();
        response = UserResponse.builder()
                .username("john")
                .firstName("Jon")
                .lastName("Doe")
                .email("s.gintoki710@gmail.com")
                .build();
    }

    @Test // Đánh dấu một phương thức là test case.
    void register_invalidRequest() {
        request.setPassword("null");
        // GIVEN: dữ liệu đã biết trước (Request và Response)
        ObjectMapper mapper = new ObjectMapper();
        try {
            String content = mapper.writeValueAsString(request);

            /*
             * Thay vì gọi tới accountService.register, sẽ trả về kết quả trực tiếp luôn với
             * Mock(object ảo)
             */
            Mockito.when(accountService.register(any())).thenReturn(response);
            // WHEN: request khi nào, THEN: Khi when xảy ra thì expect gì
            mockMvc.perform(MockMvcRequestBuilders.post("/account/register")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(content))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
                    .andExpect(MockMvcResultMatchers.jsonPath("result[0].field").value("password"))
                    .andExpect(
                            MockMvcResultMatchers.jsonPath("result[0].message")
                                    .value(
                                            "Password must have at least 16 characters, including one uppercase letter, one special character, and one number."));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void register_validRequest() {
        // GIVEN: dữ liệu đã biết trước (Request và Response)
        ObjectMapper mapper = new ObjectMapper();
        try {
            String content = mapper.writeValueAsString(request);

            /*
             * Thay vì gọi tới accountService.register, sẽ trả về kết quả trực tiếp luôn với
             * Mock(object ảo)
             */
            Mockito.when(accountService.register(any())).thenReturn(response);
            // WHEN: request khi nào, THEN: Khi when xảy ra thì expect gì
            mockMvc.perform(MockMvcRequestBuilders.post("/account/register")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(content))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                    .andExpect(MockMvcResultMatchers.jsonPath("result.username").value("john"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
