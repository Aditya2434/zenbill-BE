package com.project.backend.invoice_management_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;
import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {

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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve static resources and forward all non-API routes to index.html for React Router
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        
                        // If the requested resource exists, return it
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }
                        
                        // If it's an API route, don't serve index.html
                        if (resourcePath.startsWith("api/")) {
                            return null;
                        }
                        
                        // Otherwise, return index.html for client-side routing
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}
