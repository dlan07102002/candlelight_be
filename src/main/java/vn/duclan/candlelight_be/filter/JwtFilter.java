package vn.duclan.candlelight_be.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.exception.ErrorCode;
import vn.duclan.candlelight_be.service.JwtService;
import vn.duclan.candlelight_be.service.UserService;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private UserService userService;

    public JwtFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Lấy token từ header
        }
        return null;
    }

    private void authenticateUser(String username, String token, HttpServletRequest request) {
        UserDetails userDetails = userService.loadUserByUsername(username);
        if (userDetails != null && jwtService.validateToken(token, userDetails, false)) {
            // Tạo đối tượng Authentication nếu token hợp lệ
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken); // Lưu thông tin xác thực vào
            // SecurityContext
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Kiểm tra xem người dùng đã đăng nhập chưa
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // Nếu chưa đăng nhập, tiến hành kiểm tra JWT
                String token = extractToken(request);

                if (token != null) {
                    // Lấy username từ token JWT
                    String username = jwtService.getUsername(token);

                    if (username != null) {
                        authenticateUser(username, token, request);
                    }
                }
            }
        } catch (JwtException e) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Tiến hành các bước tiếp theo trong chuỗi xử lý filter
        filterChain.doFilter(request, response);
    }
}
