package com.project.backend.invoice_management_system.product.repository;

import com.project.backend.invoice_management_system.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds a list of all products that belong to a specific company.
     */
    List<Product> findByCompanyId(Long companyId);

    /**
     * Finds a single product by its ID AND its owning Company ID.
     */
    Optional<Product> findByIdAndCompanyId(Long productId, Long companyId);
}