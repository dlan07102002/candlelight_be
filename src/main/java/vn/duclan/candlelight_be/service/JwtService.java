package vn.duclan.candlelight_be.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import vn.duclan.candlelight_be.model.Role;
import vn.duclan.candlelight_be.model.User;

@Component
public class JwtService {
    // config secret key in application propeties
    private static final String SECRET_KEY = "9a4f2c8d3b7a1e6f45c8a0b3f267d8b1d4e6f3c8a9d2b5f8e3a9c8b5f6v8a3d9";
    private static long jwtExpirationDate = 3600000; // 1h = 3600s and 3600*1000 = 3600000 milliseconds
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
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(key(), Jwts.SIG.HS256)
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
    public Boolean isJWTExpired(String token) {
        // :: -> method reference in Java 8
        if (token == null) {
            return true;

        } else
            return getExpirationDate(token).before(new Date());
    }

    // validation Token
    public Boolean validateToken(String token, UserDetails userDetails) {
        if (token == null) {
            return false;
        }
        final String username = getUsername(token);
        return (username.equals(userDetails.getUsername()) && !isJWTExpired(token));

    }

    // Extract Username
    public String getUsername(String token) {
        // :: -> method reference in Java 8
        return getClaim(token, Claims::getSubject);
    }

}
