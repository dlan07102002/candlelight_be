package vn.duclan.candlelight_be.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import vn.duclan.candlelight_be.exception.spring_security_custom.CustomAccessDeniedHandler;
import vn.duclan.candlelight_be.exception.spring_security_custom.CustomAuthenticationEntryPoint;
import vn.duclan.candlelight_be.filter.JwtFilter;
import vn.duclan.candlelight_be.service.UserService;

@Configuration
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebSecurityConfig {
    static final String ROLE_ADMIN = "ADMIN";
    static final String ROLE_STAFF = "STAFF";
    static final String ROLE_USER = "USER";

    @Value("${fe.host}")
    @NonFinal
    String feHost;

    final JwtFilter filter;
    final CustomAccessDeniedHandler accessDeniedHandler;
    final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring", value = "datasource.driverClassName", havingValue = "com.mysql.cj.jdbc.Driver")
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider dap = new DaoAuthenticationProvider();
        dap.setUserDetailsService(userService);
        dap.setPasswordEncoder(passwordEncoder());
        return dap;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers(HttpMethod.GET, Endpoints.PUBLIC_GET_ENDPOINTS)
                .permitAll()
                // If using hasAuthority, in DB rolename is ADMIN, but if using hasRole, must
                // saved as ROLE_ADMIN. hasAuthority does not auto add prefix ROLE
                .requestMatchers(HttpMethod.POST, Endpoints.PUBLIC_POST_ENDPOINTS)
                .permitAll()

                .requestMatchers(HttpMethod.GET, Endpoints.USER_GET_ENDPOINTS)
                .hasAnyAuthority(ROLE_USER, ROLE_STAFF, ROLE_ADMIN)
                .requestMatchers(HttpMethod.POST, Endpoints.USER_POST_ENDPOINTS)
                .hasAnyAuthority(ROLE_USER, ROLE_STAFF, ROLE_ADMIN)
                .requestMatchers(HttpMethod.PATCH, Endpoints.USER_PATCH_ENDPOINTS)
                .hasAnyAuthority(ROLE_USER, ROLE_STAFF, ROLE_ADMIN)
                .requestMatchers(HttpMethod.DELETE, Endpoints.USER_DELETE_ENDPOINTS)
                .hasAnyAuthority(ROLE_USER, ROLE_STAFF, ROLE_ADMIN)
                .requestMatchers(HttpMethod.DELETE, Endpoints.ADMIN_DELETE_ENDPOINTS)
                .hasAnyAuthority(ROLE_ADMIN)

                // Admin-only
                .requestMatchers(HttpMethod.GET, Endpoints.ADMIN_GET_ENDPOINTS)
                .hasAuthority(ROLE_ADMIN)
                .requestMatchers(HttpMethod.POST, Endpoints.ADMIN_POST_ENDPOINTS)
                .hasAuthority(ROLE_ADMIN)
                .requestMatchers(HttpMethod.PATCH, Endpoints.ADMIN_PATCH_ENDPOINTS)
                .hasAuthority(ROLE_ADMIN));

        http.httpBasic(Customizer.withDefaults());

        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration corsConfig = new CorsConfiguration();
            corsConfig.addAllowedOrigin(feHost);
            corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
            corsConfig.addAllowedHeader("*");
            return corsConfig;
        }));

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // add filter
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        // Spring security default enable csrf - Cross-Site Request Forgery
        http.csrf(csrf -> csrf.disable());
        // handle exception when user access
        http.exceptionHandling(exception -> {
            exception.accessDeniedHandler(accessDeniedHandler);
            exception.authenticationEntryPoint(authenticationEntryPoint);
        });

        return http.build();
    }
}
