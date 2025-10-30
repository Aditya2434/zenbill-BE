package com.project.backend.invoice_management_system.auth.service;

import com.project.backend.invoice_management_system.auth.dto.JwtResponse;
import com.project.backend.invoice_management_system.auth.dto.LoginRequest;
import com.project.backend.invoice_management_system.auth.dto.RegisterRequest;

public interface AuthService {
    /**
     * Registers a new user and their associated company.
     *
     * @param request DTO containing user and company details
     * @return JwtResponse with a valid token
     */
    JwtResponse register(RegisterRequest request);

    /**
     * Authenticates a user and provides a token.
     *
     * @param request DTO containing login credentials
     * @return JwtResponse with a valid token
     */
    JwtResponse login(LoginRequest request);
}