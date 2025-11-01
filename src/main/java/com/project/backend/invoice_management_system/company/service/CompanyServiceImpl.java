package com.project.backend.invoice_management_system.company.service;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.common.exception.CompanyAlreadyExistsException;
import com.project.backend.invoice_management_system.common.exception.ResourceNotFoundException;
import com.project.backend.invoice_management_system.company.dto.CompanyRequest;
import com.project.backend.invoice_management_system.company.dto.CompanyResponse;
import com.project.backend.invoice_management_system.company.model.Company;
import com.project.backend.invoice_management_system.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public CompanyResponse createCompanyProfile(CompanyRequest request, User currentUser) {
        companyRepository.findByUserId(currentUser.getId()).ifPresent(existing -> {
            throw new CompanyAlreadyExistsException();
        });

        Company company = Company.builder()
                .user(currentUser)
                .companyName(request.getCompanyName())
                .companyAddress(request.getCompanyAddress())
                .city(request.getCity())
                .state(request.getState())
                .code(request.getCode())
                .gstinNo(request.getGstinNo())
                .panNumber(request.getPanNumber())
                .companyLogoUrl(request.getCompanyLogoUrl())
                .companyStampUrl(request.getCompanyStampUrl())
                .signatureUrl(request.getSignatureUrl())
                .invoicePrefix(request.getInvoicePrefix())
                .build();

        Company saved = companyRepository.save(company);
        return toResponse(saved);
    }

    @Override
    public CompanyResponse getMyCompanyProfile(User currentUser) {
        Company company = companyRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "userId", currentUser.getId()));
        return toResponse(company);
    }

    @Override
    public CompanyResponse updateMyCompanyProfile(CompanyRequest request, User currentUser) {
        Company company = companyRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "userId", currentUser.getId()));

        company.setCompanyName(request.getCompanyName());
        company.setCompanyAddress(request.getCompanyAddress());
        company.setCity(request.getCity());
        company.setState(request.getState());
        company.setCode(request.getCode());
        company.setGstinNo(request.getGstinNo());
        company.setPanNumber(request.getPanNumber());
        company.setCompanyLogoUrl(request.getCompanyLogoUrl());
        company.setCompanyStampUrl(request.getCompanyStampUrl());
        company.setSignatureUrl(request.getSignatureUrl());
        company.setInvoicePrefix(request.getInvoicePrefix());

        Company updated = companyRepository.save(company);
        return toResponse(updated);
    }

    @Override
    public CompanyResponse getCompanyById(Long companyId, User currentUser) {
        Company company = companyRepository.findByUserId(currentUser.getId())
                .filter(c -> c.getId().equals(companyId))
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
        return toResponse(company);
    }

    private CompanyResponse toResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .userId(company.getUser() != null ? company.getUser().getId() : null)
                .companyName(company.getCompanyName())
                .companyAddress(company.getCompanyAddress())
                .city(company.getCity())
                .state(company.getState())
                .code(company.getCode())
                .gstinNo(company.getGstinNo())
                .panNumber(company.getPanNumber())
                .companyLogoUrl(company.getCompanyLogoUrl())
                .companyStampUrl(company.getCompanyStampUrl())
                .signatureUrl(company.getSignatureUrl())
                .invoicePrefix(company.getInvoicePrefix())
                .build();
    }
}
