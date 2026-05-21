package com.project.backend.invoice_management_system.invoice.service;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.invoice.dto.InvoiceRequest;
import com.project.backend.invoice_management_system.invoice.dto.InvoiceResponse;
import com.project.backend.invoice_management_system.invoice.dto.InvoiceDetailResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InvoiceService {

    InvoiceResponse createInvoice(InvoiceRequest request, User currentUser);

    List<InvoiceResponse> getAllInvoices(User currentUser);

    InvoiceResponse getInvoiceById(Long invoiceId, User currentUser);

    InvoiceDetailResponse getInvoiceDetailsById(Long invoiceId, User currentUser);

    InvoiceDetailResponse updateInvoice(Long invoiceId, InvoiceRequest request, User currentUser);

    List<InvoiceResponse> getInvoicesByClientId(Long clientId, User currentUser);

    InvoiceResponse markAsPaid(Long invoiceId, User currentUser);

    // NEW METHOD
    InvoiceDetailResponse uploadPdf(Long invoiceId, MultipartFile file, User currentUser);
}