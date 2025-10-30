package com.project.backend.invoice_management_system.client.controller;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.client.dto.ClientRequest;
import com.project.backend.invoice_management_system.client.dto.ClientResponse;
import com.project.backend.invoice_management_system.client.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
    public ResponseEntity<ClientResponse> createClient(
            @Valid @RequestBody ClientRequest clientRequest,
            @AuthenticationPrincipal User user
    ) {
        ClientResponse response = clientService.createClient(clientRequest, user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Gets all clients for the logged-in user.
     */
    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAllClients(
            @AuthenticationPrincipal User user
    ) {
        List<ClientResponse> response = clientService.getAllClients(user);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets a single client by ID.
     * Our service logic ensures the user can only get their own client.
     */
    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponse> getClientById(
            @PathVariable Long clientId,
            @AuthenticationPrincipal User user
    ) {
        ClientResponse response = clientService.getClientById(clientId, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates a client by ID.
     */
    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable Long clientId,
            @Valid @RequestBody ClientRequest clientRequest,
            @AuthenticationPrincipal User user
    ) {
        ClientResponse response = clientService.updateClient(clientId, clientRequest, user);
        return ResponseEntity.ok(response);
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