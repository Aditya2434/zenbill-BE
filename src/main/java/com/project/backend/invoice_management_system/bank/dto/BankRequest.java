package com.project.backend.invoice_management_system.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankRequest {

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Account name is required")
    private String accountName;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    private String bankBranch;

    @NotBlank(message = "IFSC code is required")
    @Size(min = 11, max = 11, message = "IFSC code must be 11 characters")
    private String ifscCode;

    private boolean active = false;
}