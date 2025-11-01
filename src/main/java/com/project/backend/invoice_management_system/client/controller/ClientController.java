package com.project.backend.invoice_management_system.client.controller;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.client.dto.ClientRequest;
import com.project.backend.invoice_management_system.client.dto.ClientResponse;
import com.project.backend.invoice_management_system.client.service.ClientService;
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
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    /**
     * Creates a new client.
     * The '@AuthenticationPrincipal User user' is injected by Spring Security
     * from the JWT token.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ClientResponse>> createClient(
            @Valid @RequestBody ClientRequest clientRequest,
            @AuthenticationPrincipal User user
    ) {
        ClientResponse response = clientService.createClient(clientRequest, user);
        return ResponseBuilder.created(response);
    }

    /**
     * Gets all clients for the logged-in user.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientResponse>>> getAllClients(
            @AuthenticationPrincipal User user
    ) {
        List<ClientResponse> response = clientService.getAllClients(user);
        return ResponseBuilder.ok(response);
    }

    /**
     * Gets a single client by ID.
     * Our service logic ensures the user can only get their own client.
     */
    @GetMapping("/{clientId}")
    public ResponseEntity<ApiResponse<ClientResponse>> getClientById(
            @PathVariable Long clientId,
            @AuthenticationPrincipal User user
    ) {
        ClientResponse response = clientService.getClientById(clientId, user);
        return ResponseBuilder.ok(response);
    }

    /**
     * Updates a client by ID.
     */
    @PutMapping("/{clientId}")
    public ResponseEntity<ApiResponse<ClientResponse>> updateClient(
            @PathVariable Long clientId,
            @Valid @RequestBody ClientRequest clientRequest,
            @AuthenticationPrincipal User user
    ) {
        ClientResponse response = clientService.updateClient(clientId, clientRequest, user);
        return ResponseBuilder.ok(response);
    }

    /**
     * Deletes a client by ID.
     */
    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(
            @PathVariable Long clientId,
            @AuthenticationPrincipal User user
    ) {
        clientService.deleteClient(clientId, user);
        return ResponseEntity.noContent().build();
    }
}