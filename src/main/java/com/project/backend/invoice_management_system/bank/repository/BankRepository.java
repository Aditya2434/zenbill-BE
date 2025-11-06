package com.project.backend.invoice_management_system.bank.repository;

import com.project.backend.invoice_management_system.bank.model.BankDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<BankDetail, Long> {

    /**
     * Finds a list of all bank details that belong to a specific company.
     */
    List<BankDetail> findByCompanyId(Long companyId);

    /**
     * Finds a single bank detail by its ID AND its owning Company ID.
     */
    Optional<BankDetail> findByIdAndCompanyId(Long bankDetailId, Long companyId);

    /**
     * Checks if a bank account number already exists for a specific company.
     */
    boolean existsByAccountNumberAndCompanyId(String accountNumber, Long companyId);

    /**
     * Finds a bank detail by account number and company ID (excluding a specific bank detail ID).
     */
    Optional<BankDetail> findByAccountNumberAndCompanyIdAndIdNot(String accountNumber, Long companyId, Long excludeId);

    /**
     * Counts the number of bank accounts for a specific company.
     */
    long countByCompanyId(Long companyId);

    /**
     * Finds all active bank details for a specific company.
     */
    List<BankDetail> findByCompanyIdAndActive(Long companyId, boolean active);
}