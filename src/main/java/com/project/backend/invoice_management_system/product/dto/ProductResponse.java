package com.project.backend.invoice_management_system.product.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {

    private Long id;
    private String productName;
    private String hsnCode;
    private String uom;
    private Long companyId;
}