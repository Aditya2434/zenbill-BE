package com.project.backend.invoice_management_system.company.service;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.company.dto.CompanyRequest;
import com.project.backend.invoice_management_system.company.dto.CompanyResponse;

public interface CompanyService {

    /**
     * Creates a company profile for the currently logged-in user.
     * If a profile already exists for the user, throws an error.
     */
    CompanyResponse createCompanyProfile(CompanyRequest request, User currentUser);

    /**
     * Gets the company profile for the currently logged-in user.
     */
    CompanyResponse getMyCompanyProfile(User currentUser);

    /**
     * Updates the company profile for the currently logged-in user.
     */
    CompanyResponse updateMyCompanyProfile(CompanyRequest request, User currentUser);

    /**
     * Retrieves a company by ID, ensuring it belongs to the current user.
     */
    CompanyResponse getCompanyById(Long companyId, User currentUser);
}
