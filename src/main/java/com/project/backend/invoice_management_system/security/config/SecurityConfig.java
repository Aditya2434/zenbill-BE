package com.project.backend.invoice_management_system.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Allows for method-level security (e.g., @PreAuthorize)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CorsConfigurationSource corsConfigurationSource;

    private static final String[] PUBLIC_URLS = {
            // Public auth endpoints (login, register only)
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            // Note: /api/v1/auth/logout is protected (requires authentication)
            // Authentication is verified by attempting to access protected endpoints like /api/v1/company

            // Storage image proxy (uses token in query param for auth)
            "/api/v1/storage/image",

            // Swagger UI
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api-docs/**",
            
            // Static resources (React frontend)
            "/",
            "/index.html",
            "/favicon.ico",
            "/assets/**",
            "/static/**",
            "/*.js",
            "/*.css",
            "/*.png",
            "/*.jpg",
            "/*.svg",
            "/*.ico"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Enable CORS with our custom configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // 2. Disable CSRF (Cross-Site Request Forgery)
                // We are using stateless JWTs, so this is not needed.
                .csrf(csrf -> csrf.disable())

                // 3. Define our public (unsecured) endpoints
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated() // All other requests MUST be authenticated
                )

                // 4. Configure session management to be STATELESS
                // This is the most important part.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. Tell Spring to use our custom AuthenticationProvider
                .authenticationProvider(authenticationProvider)

                // 6. Add our JWT filter *before* the standard username/password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}