package vn.duclan.candlelight_be.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import vn.duclan.candlelight_be.dto.request.LoginRequest;
import vn.duclan.candlelight_be.dto.request.LogoutRequest;
import vn.duclan.candlelight_be.dto.request.RegisterRequest;
import vn.duclan.candlelight_be.dto.request.UpdateInfoRequest;
import vn.duclan.candlelight_be.dto.response.APIResponse;
import vn.duclan.candlelight_be.dto.response.JwtResponse;
import vn.duclan.candlelight_be.dto.response.UserResponse;
import vn.duclan.candlelight_be.dto.response.outbound.ExchangeTokenResponse;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.exception.ErrorCode;
import vn.duclan.candlelight_be.mapper.UserMapper;
import vn.duclan.candlelight_be.model.InvalidatedToken;
import vn.duclan.candlelight_be.model.Notification;
import vn.duclan.candlelight_be.model.Role;
import vn.duclan.candlelight_be.model.User;
import vn.duclan.candlelight_be.repository.InvalidatedTokenRepository;
import vn.duclan.candlelight_be.repository.RoleRepository;
import vn.duclan.candlelight_be.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountService {
    @NonFinal
    @Value("${outbound.identity.google.client-id}")
    String GG_CLIENT_ID;

    @NonFinal
    @Value("${outbound.identity.google.client-secret}")
    String GG_CLIENT_SECRET;

    @NonFinal
    @Value("${outbound.identity.google.redirect-uri}")
    String GG_REDIRECT_URI;

    @NonFinal
    @Value("${outbound.identity.github.client-id}")
    String GH_CLIENT_ID;

    @NonFinal
    @Value("${outbound.identity.github.client-secret}")
    String GH_CLIENT_SECRET;

    @NonFinal
    @Value("${outbound.identity.github.redirect-uri}")
    String GH_REDIRECT_URI;

    @NonFinal
    final String GRANT_TYPE = "authorization_code";

    final EmailService emailService;
    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final BCryptPasswordEncoder passwordEncoder;
    final UserMapper userMapper;
    final JwtService jwtService;
    final InvalidatedTokenRepository invalidatedTokenRepository;
    final AuthenticationManager authenticationManager;
    final GithubService githubService;
    final GoogleService googleService;

    @Transactional
    public UserResponse register(@Valid RegisterRequest request) {
        // Encoding password
        String encryptPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(encryptPassword);

        // set Activate code
        request.setActivateCode(generateActivateCode());

        // Insert user into DB
        User user = userMapper.toUser(request);

        List<Role> roleList = new ArrayList<>();
        roleList.add(roleRepository.findByRoleName("USER"));
        user.setRoleList(roleList);
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // send email to User for activation account
        sendActivateEmail(request.getEmail(), request.getActivateCode());

        return userMapper.toUserResponse(user);
    }

    Authentication authenticateUser(LoginRequest loginRequest) {
        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    public APIResponse<String> login(@Valid LoginRequest loginRequest) {
        APIResponse<String> apiResponse = new APIResponse<>();

        // Authenticate user
        Authentication authentication = authenticateUser(loginRequest);

        // Check isActivate
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATION));

        if (!user.getIsActivate()) {
            // "Account is not activated. Please activate your account."
            throw new AppException(ErrorCode.ACTIVATION_ERROR);
        }

        // Generate token if authentication success
        String jwt = "";
        if (authentication.isAuthenticated()) {
            jwt = jwtService.generateToken(loginRequest.getUsername());
            apiResponse.setCode(HttpStatus.OK.value());
            apiResponse.setMessage("Login successful");
            apiResponse.setResult(jwt);

        }
        return apiResponse;

    }

    public void logout(LogoutRequest request) {
        Claims claims = jwtService.getAllClaimsFromToken(request.getToken());
        String jit = claims.getId();
        Timestamp expiredTime = new Timestamp(claims.getExpiration().getTime());

        InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiredTime(expiredTime).build();
        invalidatedTokenRepository.save(invalidatedToken);
    }

    public ResponseEntity<?> activate(String email, String activateCode) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email not found"));
        if (user == null) {
            return ResponseEntity.badRequest().body("User is not exists");
        }

        if (user.getIsActivate().booleanValue()) {
            return ResponseEntity.badRequest().body(new Notification("The account has already been activated!"));
        }

        if (activateCode.equals(user.getActivateCode())) {
            user.setIsActivate(true);
            userRepository.saveAndFlush(user);
            return ResponseEntity.ok(new Notification("Activation successful"));
        } else {
            return ResponseEntity.badRequest().body(new Notification("Activation failed, Invalid activation code"));
        }
    }

    public APIResponse<UserResponse> updateInfo(String authorization, Long userId, UpdateInfoRequest request) {
        APIResponse<UserResponse> apiResponse = new APIResponse<>();

        // Get token from Header authorization field
        String token = authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;

        String username = jwtService.getUsername(token);

        User userFromToken = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATION));

        User userById = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATION));

        List<Role> roleList = userFromToken.getRoleList();
        boolean isAdmin = false;

        if (userFromToken != null && !roleList.isEmpty()) {
            for (Role role : roleList) {
                if (role.getRoleName().equals("ADMIN")) {
                    isAdmin = true;
                    break;
                }
            }
        }

        if (!username.equals(userById.getUsername()) && !isAdmin) {
            // User change another user's information
            apiResponse.setCode(HttpStatus.FORBIDDEN.value());
            apiResponse.setMessage("You are not authorized to update this user's information.");
            return apiResponse;

        } else {
            if (request.getUsername() != null
                    && !userRepository.findByUsername(request.getUsername()).isEmpty()) {
                apiResponse.setCode(ErrorCode.UNAUTHENTICATION.getCode());
                apiResponse.setMessage("This username already exists");
                return apiResponse;
            }

            if (request.getPassword() != null) {
                // Encoding password
                String encryptPassword = passwordEncoder.encode(request.getPassword());
                request.setPassword(encryptPassword);
            }

            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User Id is not valid"));
            userMapper.updateUser(user, request);
            userRepository.saveAndFlush(user);

            apiResponse.setCode(HttpStatus.OK.value());
            apiResponse.setMessage("Update Successful.");
            apiResponse.setResult(userMapper.toUserResponse(user));
            return apiResponse;
        }
    }

    public String generateActivateCode() {
        // generate random string
        return UUID.randomUUID().toString();
    }

    public void sendActivateEmail(String email, String activateCode) {
        String url = "http://localhost:5173/activate/" + email + "/" + activateCode;
        String subject = "Complete Your Registration: Activate Your Candlelight.com Account";

        String content = String.format(
                """
                        <html>
                        <head>
                            <style>
                                body { font-family: Arial, sans-serif; background-color: #f9f9f9; margin: 0; padding: 20px; }
                                .container { background-color: #ffffff; border-radius: 8px; padding: 20px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }
                                h1 { color: #333; }
                                .code { font-size: 24px; font-weight: bold; color: #ffffff; background-color: #007bff; padding: 10px; border-radius: 5px; display: inline-block; }
                                .footer { margin-top: 20px; font-size: 14px; color: #666; }
                                a { color: #007bff; text-decoration: none; }
                                a:hover { text-decoration: underline; }
                            </style>
                        </head>
                        <body>
                            <div class='container'>
                                <h1>Account Activation</h1>
                                <p>To proceed, please enter the following code to activate your account:</p>
                                <div class='code'>%s</div>
                                <p>Or click the link below to activate your account:</p>
                                <p><a href='%s'>%s</a></p>
                                <div class='footer'>Thank you for choosing Candlelight.com!</div>
                            </div>
                        </body>
                        </html>
                        """,
                activateCode, url, url);

        emailService.sendEmail("s.gintoki710@gmail.com", email, subject, content);
    }

    public JwtResponse outboundAuthenticate(String code, String type) {
        if (type.equals("github")) {
            ExchangeTokenResponse response = githubService.exchangeToken(GH_CLIENT_ID,
                    GH_CLIENT_SECRET,
                    code,
                    GH_REDIRECT_URI);
            log.info(response.getAccessToken());
            var userInfo = githubService.getUserInfo(response.getAccessToken());
            String jwtToken = jwtService.generateToken(userInfo.getLogin());

            return JwtResponse.builder().jwt(jwtToken).build();

        } else {
            var response = googleService.exchangeToken(GG_CLIENT_ID, GG_CLIENT_SECRET, code, GG_REDIRECT_URI);
            if (response == null || response.getAccessToken() == null) {
                throw new AppException(ErrorCode.UNAUTHENTICATION);
            }
            var userInfo = googleService.getUserInfo(response.getAccessToken());
            if (userInfo == null || userInfo.getEmail() == null) {
                throw new AppException(ErrorCode.UNAUTHENTICATION);
            }
            String jwtToken = jwtService.generateToken(userInfo.getEmail());
            return JwtResponse.builder().jwt(jwtToken).build();
        }

    }
}
