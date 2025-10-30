package com.project.backend.invoice_management_system.company.model;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.bank.model.BankDetail;
import com.project.backend.invoice_management_system.client.model.Client;
import com.project.backend.invoice_management_system.product.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "companies")
public class Company {

    // ... (all your other fields like id, user, companyName, etc.)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String companyName;

    @Column(length = 500)
    private String companyAddress;

    private String city;
    private String state;
    private String code; // Pincode
    private String gstinNo;
    private String panNumber;
    private String companyLogoUrl;
    private String companyStampUrl;
    private String signatureUrl;

    @Column(length = 20)
    private String invoicePrefix;

    // --- Multi-Tenancy Links (All Un-commented) ---

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Client> clients;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankDetail> bankDetails;
}