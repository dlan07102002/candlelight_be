package vn.duclan.candlelight_be.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import vn.duclan.candlelight_be.repository.RoleRepository;
import vn.duclan.candlelight_be.repository.UserRepository;
import vn.duclan.candlelight_be.dto.request.LoginRequest;
import vn.duclan.candlelight_be.dto.request.RegisterRequest;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.exception.ErrorCode;
import vn.duclan.candlelight_be.mapper.UserMapper;
import vn.duclan.candlelight_be.model.Notification;
import vn.duclan.candlelight_be.model.Role;
import vn.duclan.candlelight_be.model.User;

@Service
public class AccountService {
    private EmailServiceImpl emailService;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private UserMapper userMapper;

    @Autowired
    private JwtService jwtService;

    public AccountService(UserRepository userRepository, EmailServiceImpl emailService,
            BCryptPasswordEncoder passwordEncoder, UserMapper userMapper, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
    }

    public User register(@Valid RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            // return ResponseEntity.badRequest()
            // .body(new Notification("Username already exists. Please choose a different
            // one."));
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            // return ResponseEntity.badRequest()
            // .body(new Notification("Email already exists. Please choose a different
            // one."));
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Encoding password
        String encryptPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(encryptPassword);

        // set Activate code
        request.setActivateCode(generateActivateCode());
        request.setIsActivate(false);

        // send email to User for activation account
        sendActiveEmail(request.getEmail(), request.getActivateCode());
        User user = userMapper.toUser(request);
        Role userRole = new Role();

        userRole.setRoleName("USER");
        List<Role> roleList = new ArrayList<>();
        roleList.add(roleRepository.findByRoleName("USER"));

        user.setRoleList(roleList);
        // return ResponseEntity.ok("Registration successful!");

        // Insert user into DB
        userRepository.save(user);

        return user;
    }

    public String login(@Valid LoginRequest request) {

        String jwt = jwtService.generateToken(request.getUsername());

        return jwt;

    }

    private String generateActivateCode() {
        // generate random string
        return UUID.randomUUID().toString();
    }

    private void sendActiveEmail(String email, String activateCode) {
        String url = "http://localhost:5173/activate/" + email + "/" + activateCode;
        String subject = "Complete Your Registration: Activate Your Candlelight.com Account";

        String content = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f9f9f9; margin: 0; padding: 20px; }" +
                ".container { background-color: #ffffff; border-radius: 8px; padding: 20px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }"
                +
                "h1 { color: #333; }" +
                ".code { font-size: 24px; font-weight: bold; color: #ffffff; background-color: #007bff; padding: 10px; border-radius: 5px; display: inline-block; }"
                +
                ".footer { margin-top: 20px; font-size: 14px; color: #666; }" +
                "a { color: #007bff; text-decoration: none; }" +
                "a:hover { text-decoration: underline; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h1>Account Activation</h1>" +
                "<p>To proceed, please enter the following code to activate your account:</p>" +
                "<div class='code'>" + activateCode + "</div>" +
                "<p>Or click the link below to activate your account:</p>" +
                "<p><a href='" + url + "'>" + url + "</a></p>" +
                "<div class='footer'>Thank you for choosing Candlelight.com!</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        emailService.sendEmail("s.gintoki710@gmail.com", email, subject, content);
    }

    public ResponseEntity<?> activate(String email, String activateCode) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email not found"));
        if (user == null) {
            return ResponseEntity.badRequest().body("User is exists");
        }

        if (user.getIsActivate()) {
            return ResponseEntity.badRequest().body(new Notification("The account has already been activated!"));

        }

        if (activateCode.equals(user.getActivateCode())) {
            user.setIsActivate(true);
            userRepository.save(user);
            return ResponseEntity.ok(new Notification("Activation successful"));
        } else {
            return ResponseEntity.badRequest().body(new Notification("Activation failed, Invalid activation code"));
        }
    }
}
