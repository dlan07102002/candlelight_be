package vn.duclan.candlelight_be.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.NonFinal;
import vn.duclan.candlelight_be.dto.request.IntrospectRequest;
import vn.duclan.candlelight_be.dto.request.RefreshRequest;
import vn.duclan.candlelight_be.dto.response.IntrospectResponse;
import vn.duclan.candlelight_be.dto.response.JwtResponse;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.exception.ErrorCode;
import vn.duclan.candlelight_be.model.InvalidatedToken;
import vn.duclan.candlelight_be.model.Role;
import vn.duclan.candlelight_be.model.User;
import vn.duclan.candlelight_be.repository.InvalidatedTokenRepository;
import vn.duclan.candlelight_be.repository.UserRepository;

@Component
public class JwtService {
    // config secret key in application propeties
    @Value("${jwt.secretKey}")
    @NonFinal
    protected String SECRET_KEY;

    @Value("${jwt.valid-duration}")
    @NonFinal
    protected long VALID_DURATION;

    @Value("${jwt.refreshable-duration}")
    @NonFinal
    protected long REFRESHABLE_DURATION;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;

    // generate jwt base on username
    public String generateToken(String username) {

        Map<String, Object> claims = new HashMap<>();
        User user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATION));
        List<Role> roleList = user.getRoleList();
        boolean isAdmin = false;
        boolean isStaff = false;
        boolean isUser = false;
        if (user != null && roleList.size() > 0) {
            for (Role role : roleList) {
                if (role.getRoleName().equals("ADMIN")) {
                    isAdmin = true;
                }
                if (role.getRoleName().equals("STAFF")) {
                    isStaff = true;
                }
                if (role.getRoleName().equals("USER")) {
                    isUser = true;
                }
            }
        }

        claims.put("isAdmin", isAdmin);
        claims.put("isStaff", isStaff);
        claims.put("isUser", isUser);
        claims.put("uid", user.getUserId());

        return createToken(claims, username);
    }

    // Tạo JWT với các claim đã chọn
    private String createToken(Map<String, Object> claims, String username) {
        Date currentDate = new Date();
        // Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);
        Date expirationDate =
                new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli());

        // When call builder() and provide claims, JJWT auto convert claims map to
        // payload
        return Jwts.builder()
                .claims(claims)
                .id(UUID.randomUUID().toString())
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .signWith(key(), Jwts.SIG.HS512)
                .compact();
    }

    // Get secret key
    private SecretKey key() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        if (keyBytes.length != 64) { // For HmacSHA512, ensure the key is 64 bytes
            System.out.println(keyBytes.length);
            throw new IllegalArgumentException("Invalid key length for HS512");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // extract information
    // From 0.12.0
    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            /*
             * jjwt throws ExpiredJwtException when token expired and that's why could not
             * get Claims, catch exception help getClaims when token expired
             */
            return e.getClaims();
        }
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction) {

        Claims claims = getAllClaimsFromToken(token);

        return claimsTFunction.apply(claims);
    }

    // Extract JWT expiration date
    public Date getExpirationDate(String token) {
        // :: -> method reference in Java 8
        return getClaim(token, Claims::getExpiration);
    }

    public Date getIssueDate(String token) {
        // :: -> method reference in Java 8
        return getClaim(token, Claims::getIssuedAt);
    }

    // Extract isJWT expired
    public Boolean isJWTExpired(String token, boolean isRefresh) {
        // :: -> method reference in Java 8
        if (token == null) {
            return true;

        } else {
            Date expiredDate = isRefresh
                    ? new Date(getIssueDate(token)
                            .toInstant()
                            .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                            .toEpochMilli())
                    : getExpirationDate(token);
            return expiredDate.before(new Date());
        }
    }

    // Extract Username
    public String getUsername(String token) {
        // :: -> method reference in Java 8

        return getClaim(token, Claims::getSubject);
    }

    // validation Token
    public Boolean validateToken(String token, UserDetails userDetails, boolean isRefresh) throws JwtException {
        if (token == null) return false;
        final String username = getUsername(token);
        // Kiểm tra tính hợp lệ của token
        IntrospectResponse introspectResponse =
                introspect(IntrospectRequest.builder().token(token).build());
        if (!introspectResponse.isValid()) {
            throw new JwtException("Token invalid");
        }
        return (username.equals(userDetails.getUsername()) && !isJWTExpired(token, isRefresh));
    }

    public JwtResponse refreshToken(RefreshRequest request) {
        // Lấy token từ request
        String bearerToken = request.getToken();
        String token = bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : bearerToken;

        // Giải mã JWT và kiểm tra tính hợp lệ
        Claims claims = getAllClaimsFromToken(token);

        // Kiểm tra token đã hết hạn chưa (dựa trên REFRESHABLE_DURATION)
        if (isJWTExpired(token, true)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        String jit = claims.getId();
        // Nếu đã logout thì không thể dùng token đó để refresh
        if (invalidatedTokenRepository.existsById(jit)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        // Đánh dấu token cũ là không hợp lệ (nếu cần lưu trạng thái)
        String tokenId = claims.getId();
        invalidatedTokenRepository.save(InvalidatedToken.builder()
                .id(tokenId)
                .expiredTime(new Timestamp(claims.getExpiration().getTime()))
                .build());

        // Lấy username từ token và tạo Access Token mới
        String username = claims.getSubject();
        String newToken = generateToken(username);

        // Trả về token mới
        return JwtResponse.builder().jwt(newToken).build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        boolean isValid = true;
        String token = request.getToken();
        try {
            Claims claims = getAllClaimsFromToken(token);

            if (isJWTExpired(token, false) || invalidatedTokenRepository.existsById(claims.getId())) {
                isValid = false;
            }
        } catch (Exception e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }
}
