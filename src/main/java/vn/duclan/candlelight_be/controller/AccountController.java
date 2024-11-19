package vn.duclan.candlelight_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import vn.duclan.candlelight_be.model.User;
import vn.duclan.candlelight_be.security.JwtResponse;
import vn.duclan.candlelight_be.security.LoginRequest;
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
    public ResponseEntity<?> register(@Validated @RequestBody User user) {
        // System.out.println(user);
        // ?: unbounded wildcard.
        ResponseEntity<?> response = accountService.register(user);
        return response;
    }

    @CrossOrigin(origins = "http://localhost:5173") // Allow request from FE(Port 5173)
    @GetMapping("/activate")
    public ResponseEntity<?> activate(@RequestParam String email, @RequestParam String activateCode) {
        ResponseEntity<?> response = accountService.activate(email, activateCode);
        return response;
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // System.out.println(loginRequest.getUsername() + " " +
            // loginRequest.getPassword());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            User user = userService.findByUsername(loginRequest.getUsername());

            if (!user.getIsActivate()) {
                throw new RuntimeException("Please activate your account");
            } else {
                // Authenticated then login
                if (authentication.isAuthenticated()) {
                    final String jwt = jwtService.generateToken(loginRequest.getUsername());
                    return ResponseEntity.ok(new JwtResponse(jwt));
                }
            }

        } catch (AuthenticationException e) {
            // TODO: handle exception
            System.out.println("Authentication error: " + e);
            return ResponseEntity.badRequest().body("Invalid username or passwrod");
        }
        return ResponseEntity.badRequest().body("Failed Authentication ");
    }

}
