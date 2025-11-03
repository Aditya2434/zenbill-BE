package com.project.backend.invoice_management_system.company.service;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.common.exception.CompanyAlreadyExistsException;
import com.project.backend.invoice_management_system.common.exception.ResourceNotFoundException;
import com.project.backend.invoice_management_system.company.dto.CompanyRequest;
import com.project.backend.invoice_management_system.company.dto.CompanyResponse;
import com.project.backend.invoice_management_system.company.model.Company;
import com.project.backend.invoice_management_system.company.repository.CompanyRepository;
import com.project.backend.invoice_management_system.config.SupabaseConfig;
import com.project.backend.invoice_management_system.security.config.JwtUtils;
import com.project.backend.invoice_management_system.storage.dto.SignUrlResponse;
import com.project.backend.invoice_management_system.storage.service.SupabaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final SupabaseStorageService storageService;
    private final SupabaseConfig supabaseConfig;
    private final JwtUtils jwtUtils;

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
        return toResponse(saved, currentUser);
    }

    @Override
    public CompanyResponse getMyCompanyProfile(User currentUser) {
        Company company = companyRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "userId", currentUser.getId()));
        return toResponse(company, currentUser);
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
        return toResponse(updated, currentUser);
    }

    @Override
    public CompanyResponse getCompanyById(Long companyId, User currentUser) {
        Company company = companyRepository.findByUserId(currentUser.getId())
                .filter(c -> c.getId().equals(companyId))
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
        return toResponse(company, currentUser);
    }

    private CompanyResponse toResponse(Company company, User user) {
        String defaultBucket = supabaseConfig.getDefaultBucket();
        
        // Generate fresh token for this user
        String token = jwtUtils.generateToken(user);
        
        // Convert stored URLs to proxy URLs with token (secure, works with <img> tags)
        String logo = toProxyUrl(company.getCompanyLogoUrl(), defaultBucket, token);
        String stamp = toProxyUrl(company.getCompanyStampUrl(), defaultBucket, token);
        String signature = toProxyUrl(company.getSignatureUrl(), defaultBucket, token);

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
                .companyLogoUrl(logo)
                .companyStampUrl(stamp)
                .signatureUrl(signature)
                .invoicePrefix(company.getInvoicePrefix())
                .build();
    }

    /**
     * Converts any URL format to a secure proxy URL with JWT token.
     * Token allows <img> tags to fetch images from private bucket.
     */
    private String toProxyUrl(String stored, String defaultBucket, String token) {
        if (stored == null || stored.isBlank()) return stored;
        
        try {
            String bucket = defaultBucket;
            String path = null;
            
            // Format: "document/company/logo/uuid.png" (bucket/path)
            if (!stored.startsWith("http") && !stored.startsWith("/") && stored.contains("/")) {
                int firstSlash = stored.indexOf('/');
                bucket = stored.substring(0, firstSlash);
                path = stored.substring(firstSlash + 1);
            }
            // Already a proxy URL? Extract path and add new token
            else if (stored.contains("/api/v1/storage/image?")) {
                if (stored.contains("path=")) {
                    String pathPart = stored.split("path=")[1];
                    path = pathPart.split("&")[0];
                }
                if (stored.contains("bucket=")) {
                    String bucketPart = stored.split("bucket=")[1];
                    bucket = bucketPart.split("&")[0];
                }
            }
            // Signed URL: /storage/v1/object/sign/{bucket}/{path}?token=...
            else if (stored.contains("/storage/v1/object/sign/")) {
                String after = stored.substring(stored.indexOf("/storage/v1/object/sign/") + "/storage/v1/object/sign/".length());
                String pathPart = after.split("\\?", 2)[0];
                int firstSlash = pathPart.indexOf('/');
                if (firstSlash > 0) {
                    bucket = pathPart.substring(0, firstSlash);
                    path = pathPart.substring(firstSlash + 1);
                }
            }
            // Public URL: /storage/v1/object/public/{bucket}/{path}
            else if (stored.contains("/storage/v1/object/public/")) {
                String after = stored.substring(stored.indexOf("/storage/v1/object/public/") + "/storage/v1/object/public/".length());
                int firstSlash = after.indexOf('/');
                if (firstSlash > 0) {
                    bucket = after.substring(0, firstSlash);
                    path = after.substring(firstSlash + 1);
                }
            }
            // Bare path (no bucket prefix)
            else if (!stored.startsWith("http://") && !stored.startsWith("https://")) {
                path = stored;
            }
            
            if (path != null) {
                return "/api/v1/storage/image?bucket=" + bucket + "&path=" + path + "&token=" + token;
            }
            
            return stored;
        } catch (Exception e) {
            return stored;
        }
    }
}
