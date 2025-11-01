package com.project.backend.invoice_management_system.invoice.service;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.invoice.dto.InvoiceRequest;
import com.project.backend.invoice_management_system.invoice.dto.InvoiceResponse;
import com.project.backend.invoice_management_system.invoice.dto.InvoiceDetailResponse;

import java.util.List;

public interface InvoiceService {

    /**
     * Creates a new invoice, calculates all totals and taxes,
     * and saves it to the database.
     *
     * @param request     The DTO with all invoice data
     * @param currentUser The authenticated user
     * @return A DTO of the newly created invoice
     */
    InvoiceResponse createInvoice(InvoiceRequest request, User currentUser);

    /**
     * Retrieves all invoices for the logged-in user.
     *
     * @param currentUser The authenticated user
     * @return A list of invoice DTOs
     */
    List<InvoiceResponse> getAllInvoices(User currentUser);

    /**
     * Retrieves a single invoice by its ID.
     *
     * @param invoiceId   The ID of the invoice
     * @param currentUser The authenticated user
     * @return The invoice DTO
     */
    InvoiceResponse getInvoiceById(Long invoiceId, User currentUser);

    /**
     * Retrieves a single invoice with full details by its ID.
     */
    InvoiceDetailResponse getInvoiceDetailsById(Long invoiceId, User currentUser);

    // We will not implement Update/Delete for now, as invoices are immutable.
}