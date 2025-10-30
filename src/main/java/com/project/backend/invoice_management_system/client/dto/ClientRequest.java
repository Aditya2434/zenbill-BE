package com.project.backend.invoice_management_system.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientRequest {

    @NotBlank(message = "Client name is required")
    @Size(min = 2, max = 255)
    private String clientName;

    @Size(max = 500)
    private String clientAddress;

    private String gstinNo;
    private String state;
    private String code;
}