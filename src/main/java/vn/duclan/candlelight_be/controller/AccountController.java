package vn.duclan.candlelight_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.duclan.candlelight_be.model.User;
import vn.duclan.candlelight_be.service.AccountService;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @CrossOrigin(origins = "http://localhost:5173") // Allow request from FE(Port 5173)
    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated @RequestBody User user) {
        System.out.println(user);
        ResponseEntity<?> response = accountService.register(user);
        return response;
    }
}
