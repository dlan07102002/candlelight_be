package vn.duclan.candlelight_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.duclan.candlelight_be.dto.request.LoginRequest;
import vn.duclan.candlelight_be.dto.response.APIResponse;
import vn.duclan.candlelight_be.dto.response.JwtResponse;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.exception.ErrorCode;
import vn.duclan.candlelight_be.model.User;
import vn.duclan.candlelight_be.service.AccountService;
import vn.duclan.candlelight_be.service.JwtService;
import vn.duclan.candlelight_be.service.UserService;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @CrossOrigin(origins = "http://localhost:5173") // Allow request from FE(Port 5173)
    @PostMapping("/register")
    public APIResponse<User> register(@Validated @RequestBody User user) {
        // System.out.println(user);
        // ?: unbounded wildcard.
        APIResponse<User> apiResponse = new APIResponse<>();
        apiResponse.setResult(accountService.register(user));
        // ResponseEntity<?> response =
        return apiResponse;
    }

    @CrossOrigin(origins = "http://localhost:5173") // Allow request from FE(Port 5173)
    @GetMapping("/activate")
    public ResponseEntity<?> activate(@RequestParam String email, @RequestParam String activateCode) {
        ResponseEntity<?> response = accountService.activate(email, activateCode);
        return response;
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<APIResponse<String>> login(@RequestBody LoginRequest loginRequest) {
        APIResponse<String> apiResponse = new APIResponse<>();

        try {
            // Authenticating
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // Check isActivate
            User user = userService.findByUsername(loginRequest.getUsername());
            if (!user.getIsActivate()) {
                // "Account is not activated. Please activate your account."
                throw new AppException(ErrorCode.ACTIVATION_ERROR);
            }

            // Tạo token nếu xác thực thành công
            if (authentication.isAuthenticated()) {
                String jwt = jwtService.generateToken(loginRequest.getUsername());
                apiResponse.setCode(HttpStatus.OK.value());
                apiResponse.setMessage("Login successful");
                apiResponse.setResult(jwt);
                return ResponseEntity.ok(apiResponse);
            }

        } catch (AppException e) {
            ErrorCode errorCode = e.getErrorCode();
            apiResponse.setCode(errorCode.getCode());
            apiResponse.setMessage(errorCode.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
        } catch (AuthenticationException e) {
            apiResponse.setCode(ErrorCode.AUTHENTICATION_ERROR.getCode());
            apiResponse.setMessage(ErrorCode.AUTHENTICATION_ERROR.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

        // Trường hợp bất thường (lý do không hợp lệ)
        apiResponse.setCode(HttpStatus.UNAUTHORIZED.value());
        apiResponse.setMessage("Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

}
