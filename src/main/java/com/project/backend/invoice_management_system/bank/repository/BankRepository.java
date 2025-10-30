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
}