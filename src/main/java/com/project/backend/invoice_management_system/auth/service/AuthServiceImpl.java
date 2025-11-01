//package com.project.backend.invoice_management_system.auth.service;
//
//import com.project.backend.invoice_management_system.auth.dto.JwtResponse;
//import com.project.backend.invoice_management_system.auth.dto.LoginRequest;
//import com.project.backend.invoice_management_system.auth.dto.RegisterRequest;
//import com.project.backend.invoice_management_system.auth.model.Roles;
//import com.project.backend.invoice_management_system.auth.model.User;
//import com.project.backend.invoice_management_system.auth.repository.UserRepository;
//import com.project.backend.invoice_management_system.company.model.Company;
//import com.project.backend.invoice_management_system.company.repository.CompanyRepository;
//import com.project.backend.invoice_management_system.security.config.JwtUtils;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class AuthServiceImpl implements AuthService {
//
//    private final UserRepository userRepository;
//    private final CompanyRepository companyRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtUtils jwtUtils;
//    private final AuthenticationManager authenticationManager;
//
//    @Override
//    @Transactional // This is CRITICAL. Ensures that if Company fails, User is also rolled back.
//    public JwtResponse register(RegisterRequest request) {
//        // 1. Check if user already exists
//        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
//            throw new IllegalStateException("Email already in use. Please login.");
//        }
//
//        // 2. Create the User entity
//        User user = User.builder()
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .role(Roles.ROLE_USER)
//                .build();
//
//        // 3. Create the associated Company entity
//        Company company = Company.builder()
//                .user(user) // Link the user
//                .companyName(request.getCompanyName())
//                // Set default invoice prefix based on company name
//                .invoicePrefix(generateDefaultPrefix(request.getCompanyName()))
//                .build();
//
//        // 4. Save the user (which will be cascaded by the company)
//        // We set the user on the company, and the company on the user
//        user.setCompany(company);
//
//        // Save the User. Since Company has the 'user' field, this might
//        // be redundant depending on cascade, but it's explicit.
//        // Let's just save the Company, and the User will be saved by cascade.
//        // No, User is the parent in our model, let's save User.
//        // Wait, Company has the @JoinColumn, so Company 'owns' the relationship.
//        // We must save User first, then Company.
//        // Let's re-check...
//        // User.java has: @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
//        // Company.java has: @OneToOne @JoinColumn(name = "user_id")
//        // This means Company 'owns' the relationship.
//        // This is tricky. The 'user' field in Company is the owner.
//        // 'mappedBy' in User means it's the inverse side.
//        // We should save the User first, then save the Company.
//
//        User savedUser = userRepository.save(user);
//        company.setUser(savedUser);
//        companyRepository.save(company);
//
//
//        // 5. Generate the token
//        String jwtToken = jwtUtils.generateToken(user);
//
//        return JwtResponse.builder()
//                .token(jwtToken)
//                .email(user.getEmail())
//                .companyName(company.getCompanyName())
//                .build();
//    }
//
//    @Override
//    public JwtResponse login(LoginRequest request) {
//        // 1. Authenticate the user
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(),
//                        request.getPassword()
//                )
//        );
//
//        // 2. If authentication is successful, find the user
//        // We know the user exists, or else authenticate() would have thrown
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new IllegalStateException("Error during authentication."));
//
//        // 3. Generate the token
//        String jwtToken = jwtUtils.generateToken(user);
//
//        return JwtResponse.builder()
//                .token(jwtToken)
//                .email(user.getEmail())
//                .companyName(user.getCompany().getCompanyName())
//                .build();
//    }
//
//    private String generateDefaultPrefix(String companyName) {
//        if (companyName == null || companyName.isEmpty()) {
//            return "INV";
//        }
//        return companyName.replaceAll("[^a-zA-Z0-9]", "")
//                .substring(0, Math.min(companyName.length(), 3))
//                .toUpperCase();
//    }
//}

package com.project.backend.invoice_management_system.auth.service;

import com.project.backend.invoice_management_system.auth.dto.JwtResponse;
import com.project.backend.invoice_management_system.auth.dto.LoginRequest;
import com.project.backend.invoice_management_system.auth.dto.RegisterRequest;
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
                // --- ADD THESE TWO LINES ---
                .state(request.getState())
                .code(request.getCode())
                .build();

        user.setCompany(company);

        // This save order is correct because Company 'owns' the relationship
        // But let's be explicit and save both
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

        // We need to make sure the company is loaded
        Company company = user.getCompany();
        if (company == null) {
            // This is a safeguard
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

    private String generateDefaultPrefix(String companyName) {
        if (companyName == null || companyName.isEmpty()) {
            return "INV";
        }
        return companyName.replaceAll("[^a-zA-Z0-9]", "")
                .substring(0, Math.min(companyName.length(), 3))
                .toUpperCase();
    }
}