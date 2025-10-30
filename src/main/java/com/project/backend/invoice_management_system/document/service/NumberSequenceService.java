package com.project.backend.invoice_management_system.document.service;

import com.project.backend.invoice_management_system.company.model.Company;

public interface NumberSequenceService {

    /**
     * Generates the *next* available invoice number for a given company.
     * This method is transactional and concurrent-safe.
     *
     * @param company The company to generate the number for.
     * @return A formatted invoice number string (e.g., "PRM/2025-2026/001").
     */
    String getNextInvoiceNumber(Company company);
}