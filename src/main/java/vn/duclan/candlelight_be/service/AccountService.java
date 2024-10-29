package vn.duclan.candlelight_be.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import vn.duclan.candlelight_be.dao.UserRepository;
import vn.duclan.candlelight_be.model.Notification;
import vn.duclan.candlelight_be.model.User;

@Service
public class AccountService {
    private UserRepository userRepository;

    @Autowired
    public AccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> register(@Valid User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new Notification("Username already exists. Please choose a different one."));
        }

        if (userRepository.existsByUsername(user.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new Notification("Email already exists. Please choose a different one."));
        }

        // Insert user into DB
        User registedUser = userRepository.save(user);
        return ResponseEntity.ok("Registration successful!");

    }
}
