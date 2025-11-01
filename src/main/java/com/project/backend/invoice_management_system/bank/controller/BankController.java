package com.project.backend.invoice_management_system.bank.controller;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.bank.dto.BankRequest;
import com.project.backend.invoice_management_system.bank.dto.BankResponse;
import com.project.backend.invoice_management_system.bank.service.BankService;
import com.project.backend.invoice_management_system.common.dto.ApiResponse;
import com.project.backend.invoice_management_system.common.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/bank-details")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @PostMapping
    public ResponseEntity<ApiResponse<BankResponse>> createBankDetail(
            @Valid @RequestBody BankRequest bankRequest,
            @AuthenticationPrincipal User user
    ) {
        BankResponse response = bankService.createBankDetail(bankRequest, user);
        return ResponseBuilder.created(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BankResponse>>> getAllBankDetails(
            @AuthenticationPrincipal User user
    ) {
        List<BankResponse> response = bankService.getAllBankDetails(user);
        return ResponseBuilder.ok(response);
    }

    @GetMapping("/{bankDetailId}")
    public ResponseEntity<ApiResponse<BankResponse>> getBankDetailById(
            @PathVariable Long bankDetailId,
            @AuthenticationPrincipal User user
    ) {
        BankResponse response = bankService.getBankDetailById(bankDetailId, user);
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/{bankDetailId}")
    public ResponseEntity<ApiResponse<BankResponse>> updateBankDetail(
            @PathVariable Long bankDetailId,
            @Valid @RequestBody BankRequest bankRequest,
            @AuthenticationPrincipal User user
    ) {
        BankResponse response = bankService.updateBankDetail(bankDetailId, bankRequest, user);
        return ResponseBuilder.ok(response);
    }

    @DeleteMapping("/{bankDetailId}")
    public ResponseEntity<Void> deleteBankDetail(
            @PathVariable Long bankDetailId,
            @AuthenticationPrincipal User user
    ) {
        bankService.deleteBankDetail(bankDetailId, user);
        return ResponseEntity.noContent().build();
    }
}