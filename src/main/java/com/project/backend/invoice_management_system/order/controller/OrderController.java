package com.project.backend.invoice_management_system.order.controller;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.common.dto.ApiResponse;
import com.project.backend.invoice_management_system.common.util.ResponseBuilder;
import com.project.backend.invoice_management_system.order.dto.OrderRequest;
import com.project.backend.invoice_management_system.order.dto.OrderResponse;
import com.project.backend.invoice_management_system.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestBody OrderRequest request,
            @AuthenticationPrincipal User currentUser) {
        OrderResponse response = orderService.createOrder(request, currentUser);
        return ResponseBuilder.created(response);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByClient(
            @PathVariable Long clientId,
            @AuthenticationPrincipal User currentUser) {
        List<OrderResponse> response = orderService.getOrdersByClient(clientId, currentUser);
        return ResponseBuilder.ok(response);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal User currentUser) {
        orderService.deleteOrder(orderId, currentUser);
        return ResponseBuilder.ok(null);
    }
}