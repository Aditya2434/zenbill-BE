package com.project.backend.invoice_management_system.client.repository;

import com.project.backend.invoice_management_system.client.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Finds a list of all clients that belong to a specific company.
     * This is the core of our multi-tenant security.
     *
     * @param companyId The ID of the owning Company
     * @return A list of Clients
     */
    List<Client> findByCompanyId(Long companyId);

    /**
     * Finds a single client by its ID AND its owning Company ID.
     * This ensures a user can't fetch another user's client by guessing the ID.
     *
     * @param clientId The ID of the Client
     * @param companyId The ID of the owning Company
     * @return An Optional<Client>
     */
    Optional<Client> findByIdAndCompanyId(Long clientId, Long companyId);

    /**
     * Checks if a client with the given GSTIN already exists for a specific company.
     *
     * @param gstinNo The GSTIN number to check
     * @param companyId The ID of the owning Company
     * @return true if exists, false otherwise
     */
    boolean existsByGstinNoAndCompanyId(String gstinNo, Long companyId);

    /**
     * Finds a client by GSTIN and company ID (excluding a specific client ID).
     * Used during updates to check if the new GSTIN is already taken by another client.
     *
     * @param gstinNo The GSTIN number to check
     * @param companyId The ID of the owning Company
     * @param excludeId The ID of the client to exclude from search
     * @return An Optional<Client>
     */
    Optional<Client> findByGstinNoAndCompanyIdAndIdNot(String gstinNo, Long companyId, Long excludeId);
}