package com.project.backend.invoice_management_system.auth.service;

import com.project.backend.invoice_management_system.auth.dto.JwtResponse;
import com.project.backend.invoice_management_system.auth.dto.LoginRequest;
import com.project.backend.invoice_management_system.auth.dto.RegisterRequest;
import com.project.backend.invoice_management_system.auth.dto.ChangePasswordRequest;
import com.project.backend.invoice_management_system.auth.model.Roles;
import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.auth.repository.UserRepository;
import com.project.backend.invoice_management_system.company.model.Company;
import com.project.backend.invoice_management_system.company.repository.CompanyRepository;
import com.project.backend.invoice_management_system.security.config.JwtUtils;
import com.project.backend.invoice_management_system.common.exception.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Roles.ROLE_USER)
                .build();

        Company company = Company.builder()
                .user(user)
                .companyName(request.getCompanyName())
                .invoicePrefix(generateDefaultPrefix(request.getCompanyName()))
                .state(request.getState())
                .code(request.getCode())
                .build();

        user.setCompany(company);

        User savedUser = userRepository.save(user);
        company.setUser(savedUser);
        companyRepository.save(company);

        String jwtToken = jwtUtils.generateToken(user);

        return JwtResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .companyName(company.getCompanyName())
                .build();
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("Error during authentication."));

        Company company = user.getCompany();
        if (company == null) {
            company = companyRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new IllegalStateException("User has no associated company."));
            user.setCompany(company);
        }

        String jwtToken = jwtUtils.generateToken(user);

        return JwtResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .companyName(company.getCompanyName())
                .build();
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request, User currentUser) {
        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            throw new IllegalStateException("Incorrect old password provided.");
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }

    private String generateDefaultPrefix(String companyName) {
        if (companyName == null || companyName.isEmpty()) {
            return "INV";
        }
        return companyName.replaceAll("[^a-zA-Z0-9]", "")
                .substring(0, Math.min(companyName.length(), 3))
                .toUpperCase();
    }
}