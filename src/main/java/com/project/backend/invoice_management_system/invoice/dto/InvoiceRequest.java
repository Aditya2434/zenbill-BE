package com.project.backend.invoice_management_system.invoice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class InvoiceRequest {

    // --- Header ---
    @NotNull
    private LocalDate invoiceDate;
    private String transportMode;
    private String vehicleNo;
    private LocalDate dateOfSupply;
    private String placeOfSupply;
    private String orderNumber;
    private Boolean taxOnReverseCharge;
    private String grLrNo;
    private String eWayBillNo;

    // --- Billed To (Snapshot) ---
    @NotBlank
    private String billedToName;
    private String billedToAddress;
    private String billedToGstin;
    @NotBlank
    private String billedToState;
    @NotBlank
    private String billedToCode; // This is the client's state code for tax logic

    // --- Shipped To (Snapshot) ---
    @NotBlank
    private String shippedToName;
    private String shippedToAddress;
    private String shippedToGstin;
    private String shippedToState;
    private String shippedToCode;

    // --- Line Items ---
    @Valid // This ensures validation rules in InvoiceItemDto are checked
    @NotEmpty // The list of items cannot be empty
    private List<InvoiceItemDto> items;

    // --- Tax Rates (from user input) ---
    // User provides the rates. We calculate the amounts.
    private BigDecimal cgstRate; // e.g., 9.0
    private BigDecimal sgstRate; // e.g., 9.0
    private BigDecimal igstRate; // e.g., 18.0

    // --- Bank Details (Snapshot) ---
    // User selects a bank, frontend snapshots the details here
    private String selectedBankName;
    private String selectedAccountName;
    private String selectedAccountNumber;
    private String selectedIfscCode;

    // --- Footer ---
    private String termsAndConditions;
    // The jurisdictionCity will be pulled from the user's Company profile
}