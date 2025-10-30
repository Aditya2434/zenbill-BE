package com.project.backend.invoice_management_system.client.model;

import com.project.backend.invoice_management_system.company.model.Company;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clientName;

    @Column(length = 500)
    private String clientAddress;

    private String gstinNo;
    private String state;
    private String code; // Pincode

    /**
     * This is the multi-tenancy link.
     * It connects this Client to the Company that "owns" it.
     * We set fetch = FetchType.LAZY for performance.
     * The @JoinColumn 'company_id' will be created in this table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}