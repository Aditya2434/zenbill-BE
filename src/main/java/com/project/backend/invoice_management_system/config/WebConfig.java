package com.project.backend.invoice_management_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class WebConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins (update with your frontend URL)
        // For development, you can use "*" but it's NOT SECURE for production
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // Change to specific URL in production
        
        // Allow credentials (cookies)
        configuration.setAllowCredentials(true);
        
        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // Allow common headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Expose the Authorization header so frontend can read it
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));
        
        // How long the browser should cache CORS preflight responses (in seconds)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
