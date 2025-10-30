package com.project.backend.invoice_management_system.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 255)
    private String productName;

    private String hsnCode;

    @NotBlank(message = "Unit of Measurement (UOM) is required")
    private String uom;
}