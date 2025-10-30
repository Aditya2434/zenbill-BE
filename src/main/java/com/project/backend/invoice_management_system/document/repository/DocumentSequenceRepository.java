package com.project.backend.invoice_management_system.document.repository;

import com.project.backend.invoice_management_system.document.model.DocumentSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentSequenceRepository extends JpaRepository<DocumentSequence, Long> {

    /**
     * Finds a document sequence for a specific company and financial year.
     * We apply a PESSIMISTIC_WRITE lock, which means the database locks
     * this row so no other simultaneous transaction can read or write it
     * until this transaction is complete. This is the key to
     * preventing two users from getting the same invoice number.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ds FROM DocumentSequence ds WHERE ds.company.id = :companyId AND ds.financialYear = :financialYear")
    Optional<DocumentSequence> findByCompanyIdAndFinancialYearForUpdate(Long companyId, String financialYear);
}