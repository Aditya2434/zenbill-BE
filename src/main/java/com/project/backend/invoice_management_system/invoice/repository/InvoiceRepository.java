package com.project.backend.invoice_management_system.invoice.repository;

import com.project.backend.invoice_management_system.invoice.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * Finds a list of all invoices that belong to a specific company.
     */
    List<Invoice> findByCompanyId(Long companyId);

    /**
     * Finds a single invoice by its ID AND its owning Company ID.
     */
    Optional<Invoice> findByIdAndCompanyId(Long invoiceId, Long companyId);

    /**
     * Finds an invoice by its number, for security and validation.
     */
    Optional<Invoice> findByInvoiceNumberAndCompanyId(String invoiceNumber, Long companyId);
}