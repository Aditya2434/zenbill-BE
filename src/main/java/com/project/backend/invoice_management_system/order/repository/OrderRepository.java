package com.project.backend.invoice_management_system.order.repository;

import com.project.backend.invoice_management_system.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClientIdAndCompanyId(Long clientId, Long companyId);
}