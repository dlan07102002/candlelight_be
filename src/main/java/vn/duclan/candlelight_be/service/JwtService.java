package vn.duclan.candlelight_be.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.experimental.NonFinal;
import vn.duclan.candlelight_be.dto.request.IntrospectRequest;
import vn.duclan.candlelight_be.dto.response.IntrospectResponse;
import vn.duclan.candlelight_be.model.Role;
import vn.duclan.candlelight_be.model.User;

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
    private UserService userService;

    // generate jwt base on username
    public String generateToken(String username) {

        Map<String, Object> claims = new HashMap<>();
        User user = userService.findByUsername(username);
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
        Date expirationDate = new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli());

        // When call builder() and provide claims, JJWT auto convert claims map to
        // payload
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .signWith(key(), Jwts.SIG.HS512)
                .compact();
    }

    // Get secret key
    private SecretKey key() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // extract information
    // From 0.12.0
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction) {

        final Claims claims = getAllClaimsFromToken(token);
        return claimsTFunction.apply(claims);
    }

    // Extract JWT expiration date
    public Date getExpirationDate(String token) {
        // :: -> method reference in Java 8
        return getClaim(token, Claims::getExpiration);
    }

    // Extract isJWT expired
    public Boolean isJWTExpired(String token, boolean isRefresh) {
        // :: -> method reference in Java 8
        if (token == null) {
            return true;

        } else {
            Date expiredDate = getExpirationDate(token);
            return expiredDate.before(new Date());

        }
    }

    // validation Token
    public Boolean validateToken(String token, UserDetails userDetails) {
        if (token == null) {
            return false;
        }
        final String username = getUsername(token);
        return (username.equals(userDetails.getUsername()) && !isJWTExpired(token, false));

    }

    // Extract Username
    public String getUsername(String token) {
        // :: -> method reference in Java 8
        return getClaim(token, Claims::getSubject);
    }

    public String refreshToken(String authorization) {

        // Get token from Header authorization field
        String jwt = authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
        String username = getUsername(jwt);

        String token = generateToken(username);
        return token;
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        try {
            Claims claims = getAllClaimsFromToken(request.getToken());

            Date expiredDate = claims.getExpiration();

            return IntrospectResponse.builder().valid(!claims.isEmpty() && expiredDate.after(new Date())).build();
        } catch (SignatureException e) {
            // TODO: handle exception
            return IntrospectResponse.builder().valid(false).build();
        }

    }

}
