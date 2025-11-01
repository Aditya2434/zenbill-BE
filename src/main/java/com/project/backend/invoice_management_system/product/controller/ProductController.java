package com.project.backend.invoice_management_system.product.controller;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.product.dto.ProductRequest;
import com.project.backend.invoice_management_system.product.dto.ProductResponse;
import com.project.backend.invoice_management_system.product.service.ProductService;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest productRequest,
            @AuthenticationPrincipal User user
    ) {
        ProductResponse response = productService.createProduct(productRequest, user);
        return ResponseBuilder.created(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(
            @AuthenticationPrincipal User user
    ) {
        List<ProductResponse> response = productService.getAllProducts(user);
        return ResponseBuilder.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable Long productId,
            @AuthenticationPrincipal User user
    ) {
        ProductResponse response = productService.getProductById(productId, user);
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequest productRequest,
            @AuthenticationPrincipal User user
    ) {
        ProductResponse response = productService.updateProduct(productId, productRequest, user);
        return ResponseBuilder.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal User user
    ) {
        productService.deleteProduct(productId, user);
        return ResponseEntity.noContent().build();
    }
}