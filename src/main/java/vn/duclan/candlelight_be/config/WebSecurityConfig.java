package vn.duclan.candlelight_be.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
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

import lombok.AllArgsConstructor;
import vn.duclan.candlelight_be.exception.springSecurityCustom.CustomAccessDeniedHandler;
import vn.duclan.candlelight_be.exception.springSecurityCustom.CustomAuthenticationEntryPoint;
import vn.duclan.candlelight_be.filter.JwtFilter;
import vn.duclan.candlelight_be.service.UserService;

@Configuration
@AllArgsConstructor
public class WebSecurityConfig {
    private JwtFilter filter;
    private CustomAccessDeniedHandler accessDeniedHandler;
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Autowired
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
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http)
            throws Exception {
        http.authorizeHttpRequests(
                configurer -> configurer.requestMatchers(HttpMethod.GET, Endpoints.PUBLIC_GET_ENDPOINTS).permitAll()
                        // If using hasAuthority, in DB rolename is ADMIN, but if using hasRole, must
                        // saved as ROLE_ADMIN. hasAuthority does not auto add prefix ROLE
                        .requestMatchers(HttpMethod.POST, Endpoints.PUBLIC_POST_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.PATCH, Endpoints.USER_PATCH_ENDPOINTS)
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, Endpoints.USER_DELETE_ENDPOINTS)
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, Endpoints.ADMIN_GET_ENDPOINTS).hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, Endpoints.ADMIN_POST_ENDPOINTS).hasAuthority("ADMIN"));
        ;
        http.httpBasic(Customizer.withDefaults());

        http.cors(cors -> {
            cors.configurationSource(request -> {
                CorsConfiguration corsConfig = new CorsConfiguration();
                corsConfig.addAllowedOrigin(Endpoints.FE_HOST);
                corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
                corsConfig.addAllowedHeader("*");
                return corsConfig;
            });
        });

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // add filter
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        // Spring security default enable csrf - Cross-Site Request Forgery
        http.csrf(csrf -> csrf.disable());
        http.exceptionHandling(exception -> {
            exception.accessDeniedHandler(accessDeniedHandler);
            exception.authenticationEntryPoint(authenticationEntryPoint);
        });

        return http.build();

    }
}
