package com.project.backend.invoice_management_system.bank.service;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.bank.dto.BankRequest;
import com.project.backend.invoice_management_system.bank.dto.BankResponse;

import java.util.List;

public interface BankService {

    BankResponse createBankDetail(BankRequest bankRequest, User currentUser);

    List<BankResponse> getAllBankDetails(User currentUser);

    BankResponse getBankDetailById(Long bankDetailId, User currentUser);

    BankResponse updateBankDetail(Long bankDetailId, BankRequest bankRequest, User currentUser);

    void deleteBankDetail(Long bankDetailId, User currentUser);
}