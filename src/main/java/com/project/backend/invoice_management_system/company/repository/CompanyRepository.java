package com.project.backend.invoice_management_system.company.repository;

import com.project.backend.invoice_management_system.company.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * Finds a company profile by the associated User's ID.
     * This will be our primary way to get the profile for the logged-in user.
     */
    Optional<Company> findByUserId(Long userId);
}