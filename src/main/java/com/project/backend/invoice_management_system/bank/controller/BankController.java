package com.project.backend.invoice_management_system.bank.controller;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.bank.dto.BankRequest;
import com.project.backend.invoice_management_system.bank.dto.BankResponse;
import com.project.backend.invoice_management_system.bank.service.BankService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bank-details")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @PostMapping
    public ResponseEntity<BankResponse> createBankDetail(
            @Valid @RequestBody BankRequest bankRequest,
            @AuthenticationPrincipal User user
    ) {
        BankResponse response = bankService.createBankDetail(bankRequest, user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BankResponse>> getAllBankDetails(
            @AuthenticationPrincipal User user
    ) {
        List<BankResponse> response = bankService.getAllBankDetails(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bankDetailId}")
    public ResponseEntity<BankResponse> getBankDetailById(
            @PathVariable Long bankDetailId,
            @AuthenticationPrincipal User user
    ) {
        BankResponse response = bankService.getBankDetailById(bankDetailId, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{bankDetailId}")
    public ResponseEntity<BankResponse> updateBankDetail(
            @PathVariable Long bankDetailId,
            @Valid @RequestBody BankRequest bankRequest,
            @AuthenticationPrincipal User user
    ) {
        BankResponse response = bankService.updateBankDetail(bankDetailId, bankRequest, user);
        return ResponseEntity.ok(response);
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