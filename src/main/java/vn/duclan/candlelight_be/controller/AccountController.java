package vn.duclan.candlelight_be.controller;

import java.text.ParseException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import vn.duclan.candlelight_be.service.AccountService;
import vn.duclan.candlelight_be.service.JwtService;

@RestController
@RequestMapping("/account")
public class AccountController {
    private AccountService accountService;
    private JwtService jwtService;

    public AccountController(
            AccountService accountService,
            JwtService jwtService) {
        this.accountService = accountService;
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
            apiResponse = accountService.login(loginRequest);
            return ResponseEntity.ok(apiResponse);

        } catch (AppException e) {
            apiResponse.setCode(e.getErrorCode().getCode());
            apiResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

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
