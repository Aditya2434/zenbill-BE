package com.project.backend.invoice_management_system.company.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyResponse {

    private Long id;
    private Long userId;

    private String companyName;
    private String companyAddress;
    private String city;
    private String state;
    private String code; // Pincode
    private String gstinNo;
    private String panNumber;
    private String companyLogoUrl;
    private String companyStampUrl;
    private String signatureUrl;
    private String invoicePrefix;
}
