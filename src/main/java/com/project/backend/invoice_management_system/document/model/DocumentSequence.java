package com.project.backend.invoice_management_system.document.model;

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
@Table(name = "document_sequences",
        uniqueConstraints = {
                // This guarantees one row per company, per year
                @UniqueConstraint(columnNames = {"company_id", "financial_year"})
        }
)
public class DocumentSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "financial_year", nullable = false)
    private String financialYear;

    @Column(nullable = false)
    private Long currentNumber;
}