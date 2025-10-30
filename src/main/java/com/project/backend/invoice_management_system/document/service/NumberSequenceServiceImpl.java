package com.project.backend.invoice_management_system.document.service;

import com.project.backend.invoice_management_system.company.model.Company;
import com.project.backend.invoice_management_system.document.model.DocumentSequence;
import com.project.backend.invoice_management_system.document.repository.DocumentSequenceRepository;
import com.project.backend.invoice_management_system.document.util.FinancialYearUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NumberSequenceServiceImpl implements NumberSequenceService {

    private final DocumentSequenceRepository documentSequenceRepository;
    private final FinancialYearUtil financialYearUtil;

    @Override
    @Transactional // This is critical for the database lock to work
    public String getNextInvoiceNumber(Company company) {
        // 1. Get the current financial year
        String financialYear = financialYearUtil.getCurrentFinancialYear();

        // 2. Find the sequence for this company and year, and LOCK the row
        DocumentSequence sequence = documentSequenceRepository
                .findByCompanyIdAndFinancialYearForUpdate(company.getId(), financialYear)
                .orElseGet(() -> {
                    // If no sequence exists, create a new one starting at 0
                    return new DocumentSequence(null, company, financialYear, 0L);
                });

        // 3. Increment the number
        long nextNumber = sequence.getCurrentNumber() + 1;
        sequence.setCurrentNumber(nextNumber);

        // 4. Save the updated sequence (this releases the lock)
        documentSequenceRepository.save(sequence);

        // 5. Format the final number string
        // e.g., "PRM" + "/" + "2025-2026" + "/" + "001"
        return String.format("%s/%s/%03d",
                company.getInvoicePrefix(),
                financialYear,
                nextNumber
        );
    }
}