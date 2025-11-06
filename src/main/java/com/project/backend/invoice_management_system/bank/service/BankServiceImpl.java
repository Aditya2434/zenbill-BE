package com.project.backend.invoice_management_system.bank.service;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.bank.dto.BankRequest;
import com.project.backend.invoice_management_system.bank.dto.BankResponse;
import com.project.backend.invoice_management_system.bank.model.BankDetail;
import com.project.backend.invoice_management_system.bank.repository.BankRepository;
import com.project.backend.invoice_management_system.common.exception.ResourceNotFoundException;
import com.project.backend.invoice_management_system.company.model.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;

    @Override
    public BankResponse createBankDetail(BankRequest bankRequest, User currentUser) {
        Company company = getCompanyFromUser(currentUser);

        // Check if company has reached the maximum limit of 5 bank accounts
        long bankAccountCount = bankRepository.countByCompanyId(company.getId());
        if (bankAccountCount >= 5) {
            throw new IllegalStateException("Maximum limit reached. A user can only have 5 bank accounts.");
        }

        // Check if bank account number already exists for this company
        if (bankRepository.existsByAccountNumberAndCompanyId(bankRequest.getAccountNumber(), company.getId())) {
            throw new IllegalStateException("Bank account number already exists for this company. Please use a different account number.");
        }

        // If this account is being set as active, deactivate all other accounts
        if (bankRequest.isActive()) {
            deactivateAllAccountsForCompany(company.getId());
        }

        BankDetail bankDetail = BankDetail.builder()
                .bankName(bankRequest.getBankName())
                .accountName(bankRequest.getAccountName())
                .accountNumber(bankRequest.getAccountNumber())
                .bankBranch(bankRequest.getBankBranch())
                .ifscCode(bankRequest.getIfscCode())
                .active(bankRequest.isActive())
                .company(company) // The multi-tenancy link
                .build();

        BankDetail savedBankDetail = bankRepository.save(bankDetail);
        return bankDetailToResponse(savedBankDetail);
    }

    @Override
    public List<BankResponse> getAllBankDetails(User currentUser) {
        Company company = getCompanyFromUser(currentUser);
        return bankRepository.findByCompanyId(company.getId())
                .stream()
                .map(this::bankDetailToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BankResponse getBankDetailById(Long bankDetailId, User currentUser) {
        Company company = getCompanyFromUser(currentUser);
        BankDetail bankDetail = bankRepository.findByIdAndCompanyId(bankDetailId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("BankDetail", "id", bankDetailId));
        return bankDetailToResponse(bankDetail);
    }

    @Override
    public BankResponse updateBankDetail(Long bankDetailId, BankRequest bankRequest, User currentUser) {
        Company company = getCompanyFromUser(currentUser);
        BankDetail bankDetail = bankRepository.findByIdAndCompanyId(bankDetailId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("BankDetail", "id", bankDetailId));

        // Check if the new account number already exists for this company (excluding current record)
        Optional<BankDetail> existingBankDetail = bankRepository.findByAccountNumberAndCompanyIdAndIdNot(
                bankRequest.getAccountNumber(), company.getId(), bankDetailId);
        if (existingBankDetail.isPresent()) {
            throw new IllegalStateException("Bank account number already exists for this company. Please use a different account number.");
        }

        // If this account is being set as active, deactivate all other accounts
        if (bankRequest.isActive() && !bankDetail.isActive()) {
            deactivateAllAccountsForCompany(company.getId());
        }

        bankDetail.setBankName(bankRequest.getBankName());
        bankDetail.setAccountName(bankRequest.getAccountName());
        bankDetail.setAccountNumber(bankRequest.getAccountNumber());
        bankDetail.setBankBranch(bankRequest.getBankBranch());
        bankDetail.setIfscCode(bankRequest.getIfscCode());
        bankDetail.setActive(bankRequest.isActive());

        BankDetail updatedBankDetail = bankRepository.save(bankDetail);
        return bankDetailToResponse(updatedBankDetail);
    }

    @Override
    public void deleteBankDetail(Long bankDetailId, User currentUser) {
        Company company = getCompanyFromUser(currentUser);
        BankDetail bankDetail = bankRepository.findByIdAndCompanyId(bankDetailId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("BankDetail", "id", bankDetailId));
        bankRepository.delete(bankDetail);
    }

    // --- HELPER METHODS ---

    private Company getCompanyFromUser(User user) {
        if (user.getCompany() == null) {
            throw new IllegalStateException("User is not associated with a company.");
        }
        return user.getCompany();
    }

    private BankResponse bankDetailToResponse(BankDetail bankDetail) {
        return BankResponse.builder()
                .id(bankDetail.getId())
                .bankName(bankDetail.getBankName())
                .accountName(bankDetail.getAccountName())
                .accountNumber(bankDetail.getAccountNumber())
                .bankBranch(bankDetail.getBankBranch())
                .ifscCode(bankDetail.getIfscCode())
                .active(bankDetail.isActive())
                .companyId(bankDetail.getCompany().getId())
                .build();
    }

    /**
     * Deactivates all bank accounts for a specific company.
     */
    private void deactivateAllAccountsForCompany(Long companyId) {
        List<BankDetail> activeAccounts = bankRepository.findByCompanyIdAndActive(companyId, true);
        for (BankDetail account : activeAccounts) {
            account.setActive(false);
        }
        if (!activeAccounts.isEmpty()) {
            bankRepository.saveAll(activeAccounts);
        }
    }
}