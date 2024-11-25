package vn.duclan.candlelight_be.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.exception.ErrorCode;
import vn.duclan.candlelight_be.service.JwtService;
import vn.duclan.candlelight_be.service.UserService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

// Check everytime client send Request
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private void authenticateUser(String username, String token, HttpServletRequest request) {
        UserDetails userDetails = userService.loadUserByUsername(username);
        if (userDetails != null && jwtService.validateToken(token, userDetails)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = extractToken(request);

            if (token != null) {

                String username = jwtService.getUsername(token);
                System.out.println(username);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    authenticateUser(username, token, request);
                }
            }
        } catch (JwtException e) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        filterChain.doFilter(request, response);
    }

}
