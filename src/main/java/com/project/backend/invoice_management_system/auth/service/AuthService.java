package com.project.backend.invoice_management_system.auth.service;

import com.project.backend.invoice_management_system.auth.dto.JwtResponse;
import com.project.backend.invoice_management_system.auth.dto.LoginRequest;
import com.project.backend.invoice_management_system.auth.dto.RegisterRequest;
import com.project.backend.invoice_management_system.auth.dto.ChangePasswordRequest;
import com.project.backend.invoice_management_system.auth.model.User;

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

    /**
     * Changes the password for the currently authenticated user.
     *
     * @param request DTO containing old and new password
     * @param currentUser The authenticated user
     */
    void changePassword(ChangePasswordRequest request, User currentUser);

    /**
     * Resets the password for the given email to a temporary one.
     *
     * @param email The user's email
     * @return The new temporary password
     */
    String forgotPassword(String email);
}