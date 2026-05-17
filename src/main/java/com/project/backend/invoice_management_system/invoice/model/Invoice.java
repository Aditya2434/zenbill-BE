package com.project.backend.invoice_management_system.invoice.model;

import com.project.backend.invoice_management_system.company.model.Company;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false)
    private LocalDate invoiceDate;

    private String transportMode;
    private String vehicleNo;
    private LocalDate dateOfSupply;
    private String placeOfSupply;
    private String orderNumber;
    private Boolean taxOnReverseCharge;
    private String grLrNo;
    private String eWayBillNo;

    @Column(nullable = false)
    private String billedToName;
    @Column(length = 500)
    private String billedToAddress;
    private String billedToGstin;
    private String billedToState;
    private String billedToCode;

    @Column(nullable = false)
    private String shippedToName;
    @Column(length = 500)
    private String shippedToAddress;
    private String shippedToGstin;
    private String shippedToState;
    private String shippedToCode;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<InvoiceItem> items;

    private BigDecimal totalAmountBeforeTax;
    private BigDecimal cgstRate;
    private BigDecimal cgstAmount;
    private BigDecimal sgstRate;
    private BigDecimal sgstAmount;
    private BigDecimal igstRate;
    private BigDecimal igstAmount;
    private BigDecimal totalTaxAmount;
    @Column(nullable = false)
    private BigDecimal totalAmountAfterTax;
    @Column(length = 1000)
    private String totalAmountInWords;

    private String selectedBankName;
    private String selectedAccountName;
    private String selectedAccountNumber;
    private String selectedIfscCode;

    private String jurisdictionCity;
    @Column(length = 2000)
    private String termsAndConditions;

    private String pdfUrl;

    // Tracks payment status (e.g., "Unpaid", "Paid")
    private String status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}