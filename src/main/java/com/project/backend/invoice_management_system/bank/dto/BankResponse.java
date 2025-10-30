package com.project.backend.invoice_management_system.bank.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankResponse {

    private Long id;
    private String bankName;
    private String accountName;
    private String accountNumber;
    private String bankBranch;
    private String ifscCode;
    private Long companyId;
}