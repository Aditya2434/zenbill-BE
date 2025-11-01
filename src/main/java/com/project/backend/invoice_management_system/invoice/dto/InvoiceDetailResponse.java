package com.project.backend.invoice_management_system.invoice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class InvoiceDetailResponse {

    private Long id;
    private Long companyId;
    private String invoiceNumber;

    // Header
    private LocalDate invoiceDate;
    private String transportMode;
    private String vehicleNo;
    private LocalDate dateOfSupply;
    private String placeOfSupply;
    private String orderNumber;
    private Boolean taxOnReverseCharge;
    private String grLrNo;
    private String eWayBillNo;

    // Billed To
    private String billedToName;
    private String billedToAddress;
    private String billedToGstin;
    private String billedToState;
    private String billedToCode;

    // Shipped To
    private String shippedToName;
    private String shippedToAddress;
    private String shippedToGstin;
    private String shippedToState;
    private String shippedToCode;

    // Items (as requested fields)
    private List<InvoiceItemDto> items;

    // Tax rates and amounts
    private BigDecimal cgstRate;
    private BigDecimal cgstAmount;
    private BigDecimal sgstRate;
    private BigDecimal sgstAmount;
    private BigDecimal igstRate;
    private BigDecimal igstAmount;

    // Totals
    private BigDecimal totalAmountBeforeTax;
    private BigDecimal totalTaxAmount;
    private BigDecimal totalAmountAfterTax;
    private String totalAmountInWords;

    // Bank snapshot
    private String selectedBankName;
    private String selectedAccountName;
    private String selectedAccountNumber;
    private String selectedIfscCode;

    // Footer
    private String termsAndConditions;
    private String jurisdictionCity;

    private String pdfUrl;
}


