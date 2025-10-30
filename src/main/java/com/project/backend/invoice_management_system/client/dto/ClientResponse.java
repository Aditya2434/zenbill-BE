package com.project.backend.invoice_management_system.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientResponse {

    private Long id;
    private String clientName;
    private String clientAddress;
    private String gstinNo;
    private String state;
    private String code;
    private Long companyId; // Good to include for context
}