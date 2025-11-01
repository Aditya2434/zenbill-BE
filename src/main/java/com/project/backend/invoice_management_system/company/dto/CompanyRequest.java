package com.project.backend.invoice_management_system.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyRequest {

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 255)
    private String companyName;

    @Size(max = 500)
    private String companyAddress;

    private String city;
    private String state;
    private String code; // Pincode
    private String gstinNo;
    private String panNumber;
    private String companyLogoUrl;
    private String companyStampUrl;
    private String signatureUrl;

    @Size(max = 20)
    private String invoicePrefix;
}
