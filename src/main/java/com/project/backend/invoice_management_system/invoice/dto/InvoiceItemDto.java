package com.project.backend.invoice_management_system.invoice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class InvoiceItemDto {

    // These fields are snapshots
    @NotBlank
    private String description;
    private String hsnCode;
    @NotBlank
    private String uom;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal quantity;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal rate;

    // The 'amount' (quantity * rate) will be calculated on the backend
}