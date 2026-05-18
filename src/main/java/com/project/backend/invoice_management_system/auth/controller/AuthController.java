package com.project.backend.invoice_management_system.auth.controller;

import com.project.backend.invoice_management_system.auth.dto.JwtResponse;
import com.project.backend.invoice_management_system.auth.dto.LoginRequest;
import com.project.backend.invoice_management_system.auth.dto.RegisterRequest;
import com.project.backend.invoice_management_system.auth.dto.ChangePasswordRequest;
import com.project.backend.invoice_management_system.auth.dto.ForgotPasswordRequest;
import com.project.backend.invoice_management_system.auth.service.AuthService;
import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.common.dto.ApiResponse;
import com.project.backend.invoice_management_system.common.util.ResponseBuilder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.expiration-ms:2764800000}") // Default to 32 days
    private long jwtExpirationMs;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<JwtResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        JwtResponse jwtResponse = authService.register(request);
        setAuthCookie(response, jwtResponse.getToken());
        return ResponseBuilder.created(jwtResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        JwtResponse jwtResponse = authService.login(request);
        setAuthCookie(response, jwtResponse.getToken());
        return ResponseBuilder.ok(jwtResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        clearAuthCookie(response);
        return ResponseBuilder.ok("Logged out successfully");
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal User user
    ) {
        authService.changePassword(request, user);
        return ResponseBuilder.ok("Password changed successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        String newPassword = authService.forgotPassword(request.getEmail());
        // Note: For production, do NOT return the password in the response. Send an email instead.
        return ResponseBuilder.ok("Password reset successfully. Your new temporary password is: " + newPassword);
    }

    private void setAuthCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("authToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtExpirationMs / 1000));
        response.addCookie(cookie);
    }

    private void clearAuthCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("authToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}