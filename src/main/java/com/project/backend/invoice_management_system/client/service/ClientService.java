package com.project.backend.invoice_management_system.client.service;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.client.dto.ClientRequest;
import com.project.backend.invoice_management_system.client.dto.ClientResponse;

import java.util.List;

public interface ClientService {

    /**
     * Creates a new client for the currently logged-in user.
     *
     * @param clientRequest The DTO with new client details.
     * @param currentUser   The authenticated user principal.
     * @return The newly created client as a DTO.
     */
    ClientResponse createClient(ClientRequest clientRequest, User currentUser);

    /**
     * Retrieves all clients belonging to the currently logged-in user.
     *
     * @param currentUser The authenticated user principal.
     * @return A list of client DTOs.
     */
    List<ClientResponse> getAllClients(User currentUser);

    /**
     * Retrieves a single client by its ID, ensuring it belongs to the logged-in user.
     *
     * @param clientId    The ID of the client to retrieve.
     * @param currentUser The authenticated user principal.
     * @return The client DTO.
     */
    ClientResponse getClientById(Long clientId, User currentUser);

    /**
     * Updates an existing client, ensuring it belongs to the logged-in user.
     *
     * @param clientId      The ID of the client to update.
     * @param clientRequest The DTO with updated client details.
     * @param currentUser   The authenticated user principal.
     * @return The updated client DTO.
     */
    ClientResponse updateClient(Long clientId, ClientRequest clientRequest, User currentUser);

    /**
     * Deletes a client, ensuring it belongs to the logged-in user.
     *
     * @param clientId    The ID of the client to delete.
     * @param currentUser The authenticated user principal.
     */
    void deleteClient(Long clientId, User currentUser);
}