package com.project.backend.invoice_management_system.invoice.controller;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.invoice.dto.InvoiceRequest;
import com.project.backend.invoice_management_system.invoice.dto.InvoiceResponse;
import com.project.backend.invoice_management_system.invoice.dto.InvoiceDetailResponse;
import com.project.backend.invoice_management_system.invoice.service.InvoiceService;
import com.project.backend.invoice_management_system.common.dto.ApiResponse;
import com.project.backend.invoice_management_system.common.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @Valid @RequestBody InvoiceRequest request,
            @AuthenticationPrincipal User user
    ) {
        InvoiceResponse response = invoiceService.createInvoice(request, user);
        return ResponseBuilder.created(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getAllInvoices(
            @AuthenticationPrincipal User user
    ) {
        List<InvoiceResponse> response = invoiceService.getAllInvoices(user);
        return ResponseBuilder.ok(response);
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(
            @PathVariable Long invoiceId,
            @AuthenticationPrincipal User user
    ) {
        InvoiceResponse response = invoiceService.getInvoiceById(invoiceId, user);
        return ResponseBuilder.ok(response);
    }

    @GetMapping("/{invoiceId}/details")
    public ResponseEntity<ApiResponse<InvoiceDetailResponse>> getInvoiceDetails(
            @PathVariable Long invoiceId,
            @AuthenticationPrincipal User user
    ) {
        InvoiceDetailResponse response = invoiceService.getInvoiceDetailsById(invoiceId, user);
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/{invoiceId}")
    public ResponseEntity<ApiResponse<InvoiceDetailResponse>> updateInvoice(
            @PathVariable Long invoiceId,
            @Valid @RequestBody InvoiceRequest request,
            @AuthenticationPrincipal User user
    ) {
        InvoiceDetailResponse response = invoiceService.updateInvoice(invoiceId, request, user);
        return ResponseBuilder.ok(response);
    }
}