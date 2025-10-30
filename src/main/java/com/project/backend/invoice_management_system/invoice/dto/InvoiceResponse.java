package com.project.backend.invoice_management_system.invoice.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class InvoiceResponse {

    private Long id;
    private Long companyId;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String billedToName;
    private BigDecimal totalAmountAfterTax;
    private String pdfUrl;
}