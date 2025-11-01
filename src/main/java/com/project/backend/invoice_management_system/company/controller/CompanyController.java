package com.project.backend.invoice_management_system.company.controller;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.common.dto.ApiResponse;
import com.project.backend.invoice_management_system.common.util.ResponseBuilder;
import com.project.backend.invoice_management_system.company.dto.CompanyRequest;
import com.project.backend.invoice_management_system.company.dto.CompanyResponse;
import com.project.backend.invoice_management_system.company.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    /**
     * Get the current user's company profile.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> getMyCompany(
            @AuthenticationPrincipal User user
    ) {
        CompanyResponse response = companyService.getMyCompanyProfile(user);
        return ResponseBuilder.ok(response);
    }

    /**
     * Update the current user's company profile.
     */
    @PutMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> updateMyCompany(
            @Valid @RequestBody CompanyRequest request,
            @AuthenticationPrincipal User user
    ) {
        CompanyResponse response = companyService.updateMyCompanyProfile(request, user);
        return ResponseBuilder.ok(response);
    }

    /**
     * Get a company by ID (only if it belongs to the current user).
     */
    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyById(
            @PathVariable Long companyId,
            @AuthenticationPrincipal User user
    ) {
        CompanyResponse response = companyService.getCompanyById(companyId, user);
        return ResponseBuilder.ok(response);
    }
}
