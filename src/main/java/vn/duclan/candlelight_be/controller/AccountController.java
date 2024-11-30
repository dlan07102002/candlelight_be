package vn.duclan.candlelight_be.controller;

import java.text.ParseException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.duclan.candlelight_be.dto.request.IntrospectRequest;
import vn.duclan.candlelight_be.dto.request.LoginRequest;
import vn.duclan.candlelight_be.dto.request.LogoutRequest;
import vn.duclan.candlelight_be.dto.request.RefreshRequest;
import vn.duclan.candlelight_be.dto.request.RegisterRequest;
import vn.duclan.candlelight_be.dto.request.UpdateInfoRequest;
import vn.duclan.candlelight_be.dto.response.APIResponse;
import vn.duclan.candlelight_be.dto.response.IntrospectResponse;
import vn.duclan.candlelight_be.dto.response.JwtResponse;
import vn.duclan.candlelight_be.dto.response.UserResponse;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.exception.ErrorCode;
import vn.duclan.candlelight_be.model.User;
import vn.duclan.candlelight_be.service.AccountService;
import vn.duclan.candlelight_be.service.JwtService;
import vn.duclan.candlelight_be.service.UserService;

@RestController
@RequestMapping("/account")
public class AccountController {
    private AccountService accountService;
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private JwtService jwtService;

    public AccountController(
            AccountService accountService,
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtService jwtService) {
        this.accountService = accountService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @CrossOrigin(origins = "http://localhost:5173") // Allow request from FE(Port 5173)
    @PostMapping("/register")
    public APIResponse<UserResponse> register(@Validated @RequestBody RegisterRequest request) {

        // ?: unbounded wildcard.
        APIResponse<UserResponse> apiResponse = new APIResponse<>();
        apiResponse.setResult(accountService.register(request));
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
                String jwt = accountService.login(loginRequest);
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
            System.out.println(e.getMessage());
            apiResponse.setCode(ErrorCode.UNAUTHENTICATION.getCode());
            apiResponse.setMessage(ErrorCode.UNAUTHENTICATION.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

        // Trường hợp bất thường (lý do không hợp lệ)
        apiResponse.setCode(HttpStatus.UNAUTHORIZED.value());
        apiResponse.setMessage("Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    @PostMapping("/logout")
    public APIResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException {
        accountService.logout(request);
        return APIResponse.<Void>builder().build();
    }

    @PatchMapping("/update/{userId}")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<APIResponse<UserResponse>> update(
            @RequestBody UpdateInfoRequest request,
            @PathVariable String userId,
            @RequestHeader("Authorization") String authorization) {
        APIResponse<UserResponse> apiResponse = accountService.updateInfo(authorization, Integer.parseInt(userId),
                request);

        if (apiResponse.getResult() != null)
            return ResponseEntity.ok(apiResponse);
        else
            return ResponseEntity.badRequest().body(apiResponse);
    }

    @PostMapping("/refresh")
    @CrossOrigin(origins = "http://localhost:5173")
    public APIResponse<JwtResponse> refresh(@RequestBody RefreshRequest request) {
        APIResponse<JwtResponse> apiResponse = new APIResponse<>();
        JwtResponse jwtResponse = jwtService.refreshToken(request);
        apiResponse.setCode(HttpStatus.OK.value());
        apiResponse.setMessage("Refresh successful");
        apiResponse.setResult(jwtResponse);
        return apiResponse;
    }

    @PostMapping("/introspect")
    public APIResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) {
        var result = jwtService.introspect(request);
        return APIResponse.<IntrospectResponse>builder().result(result).build();
    }
}
