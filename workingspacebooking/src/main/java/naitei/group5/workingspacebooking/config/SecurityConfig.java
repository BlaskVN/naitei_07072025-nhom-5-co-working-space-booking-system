package naitei.group5.workingspacebooking.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static naitei.group5.workingspacebooking.constant.Endpoint.*;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // Gom các route của renter
    public static final String[] RENTER_ROUTES = {
            RENTER_VENUES_SUB  // /api/venues/** 
    };

    // Gom các route của owner
    public static final String[] OWNER_ROUTES = {
            OWNER_VENUES
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                // Admin login routes
                                "/", "/admin/login",
                                // Static resources
                                "/css/**", "/js/**", "/images/**", "/static/**",
                                // Error pages
                                "/error", "/admin/error/**",
                                // Admin error test routes (dev only)
                                "/admin/test-error/**",
                                // Existing API auth endpoints
                                AUTH_LOGIN,
                                AUTH_REFRESH,
                                AUTH_LOGOUT,
                                AUTH_RECOVERY,
                                AUTH_REGISTER_RENTER,
                                AUTH_REGISTER_OWNER
                        ).permitAll()
                        // Admin routes require ADMIN role
                        .requestMatchers(ADMIN_BASE).hasRole("ADMIN")
                        // Renter routes require RENTER role
                        .requestMatchers(RENTER_ROUTES).hasRole("RENTER")
                        // Owner routes require OWNER role
                        .requestMatchers(OWNER_ROUTES).hasRole("OWNER")
                        // Tất cả request còn lại phải login
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Xử lý khi chưa đăng nhập
                            if (request.getRequestURI().startsWith("/admin")) {
                                response.sendRedirect("/admin/login");
                            } else {
                                response.sendError(401, "Unauthorized");
                            }
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // Xử lý khi không có quyền
                            if (request.getRequestURI().startsWith("/admin")) {
                                response.sendRedirect("/admin/error/403");
                            } else {
                                response.sendError(403, "Access Denied");
                            }
                        })
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
