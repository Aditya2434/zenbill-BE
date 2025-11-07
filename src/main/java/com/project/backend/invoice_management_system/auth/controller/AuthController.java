package com.project.backend.invoice_management_system.auth.controller;

import com.project.backend.invoice_management_system.auth.dto.JwtResponse;
import com.project.backend.invoice_management_system.auth.dto.LoginRequest;
import com.project.backend.invoice_management_system.auth.dto.RegisterRequest;
import com.project.backend.invoice_management_system.auth.service.AuthService;
import com.project.backend.invoice_management_system.common.dto.ApiResponse;
import com.project.backend.invoice_management_system.common.util.ResponseBuilder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.expiration-ms:2764800000}") // Default to 32 days
    private long jwtExpirationMs;

    /**
     * Endpoint for new user registration.
     * Sets JWT token as httpOnly cookie.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<JwtResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        JwtResponse jwtResponse = authService.register(request);
        setAuthCookie(response, jwtResponse.getToken());
        return ResponseBuilder.created(jwtResponse);
    }

    /**
     * Endpoint for user login.
     * Sets JWT token as httpOnly cookie.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        JwtResponse jwtResponse = authService.login(request);
        setAuthCookie(response, jwtResponse.getToken());
        return ResponseBuilder.ok(jwtResponse);
    }

    /**
     * OPTIONAL: Endpoint to verify authentication and get current user info.
     * Uses the JWT from cookie (handled by JwtAuthFilter).
     * 
     * NOTE: Currently not used. Authentication is verified by accessing protected endpoints.
     * Uncomment if you need a dedicated endpoint to check auth status.
     */
    /*
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(
                ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Not authenticated")
                    .build()
            );
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", authentication.getName());
        userInfo.put("authenticated", true);
        
        return ResponseBuilder.ok(userInfo);
    }
    */

    /**
     * Endpoint to logout user by clearing the auth cookie.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        clearAuthCookie(response);
        return ResponseBuilder.ok("Logged out successfully");
    }

    /**
     * Helper method to set the authentication cookie.
     */
    private void setAuthCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("authToken", token);
        cookie.setHttpOnly(true); // Prevents JavaScript access (XSS protection)
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/"); // Available for all paths
        cookie.setMaxAge((int) (jwtExpirationMs / 1000)); // Convert ms to seconds
        // cookie.setSameSite("Lax"); // Requires Servlet 6.0+ / Spring Boot 3.1+
        
        response.addCookie(cookie);
    }

    /**
     * Helper method to clear the authentication cookie.
     */
    private void clearAuthCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("authToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire immediately
        
        response.addCookie(cookie);
    }
}