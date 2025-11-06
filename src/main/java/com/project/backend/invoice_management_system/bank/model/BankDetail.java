package com.project.backend.invoice_management_system.bank.model;

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
@Table(name = "bank_details")
public class BankDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false)
    private String accountNumber;

    private String bankBranch;

    @Column(nullable = false)
    private String ifscCode;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean active = false;

    /**
     * The multi-tenancy link to the owning Company.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}