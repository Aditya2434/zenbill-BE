package com.project.backend.invoice_management_system.client.service;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.client.dto.ClientRequest;
import com.project.backend.invoice_management_system.client.dto.ClientResponse;
import com.project.backend.invoice_management_system.client.model.Client;
import com.project.backend.invoice_management_system.client.repository.ClientRepository;
import com.project.backend.invoice_management_system.common.exception.ResourceNotFoundException;
import com.project.backend.invoice_management_system.company.model.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    // We don't need the CompanyRepository, we can get the Company from the User.

    @Override
    public ClientResponse createClient(ClientRequest clientRequest, User currentUser) {
        // 1. Get the company of the logged-in user
        Company company = getCompanyFromUser(currentUser);

        // 2. Create a new Client entity from the DTO
        Client client = Client.builder()
                .clientName(clientRequest.getClientName())
                .clientAddress(clientRequest.getClientAddress())
                .gstinNo(clientRequest.getGstinNo())
                .state(clientRequest.getState())
                .code(clientRequest.getCode())
                .company(company) // 3. Set the multi-tenancy link
                .build();

        // 4. Save to the database
        Client savedClient = clientRepository.save(client);

        // 5. Convert to a DTO and return
        return clientToResponse(savedClient);
    }

    @Override
    public List<ClientResponse> getAllClients(User currentUser) {
        Company company = getCompanyFromUser(currentUser);

        // This is where our custom repository method shines.
        // We ONLY find clients for this user's company.
        return clientRepository.findByCompanyId(company.getId())
                .stream()
                .map(this::clientToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClientResponse getClientById(Long clientId, User currentUser) {
        Company company = getCompanyFromUser(currentUser);

        // Here we use the secure finder method
        Client client = clientRepository.findByIdAndCompanyId(clientId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        return clientToResponse(client);
    }

    @Override
    public ClientResponse updateClient(Long clientId, ClientRequest clientRequest, User currentUser) {
        Company company = getCompanyFromUser(currentUser);

        // 1. Find the client securely
        Client client = clientRepository.findByIdAndCompanyId(clientId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        // 2. Update the fields
        client.setClientName(clientRequest.getClientName());
        client.setClientAddress(clientRequest.getClientAddress());
        client.setGstinNo(clientRequest.getGstinNo());
        client.setState(clientRequest.getState());
        client.setCode(clientRequest.getCode());

        // 3. Save the updated entity
        Client updatedClient = clientRepository.save(client);

        return clientToResponse(updatedClient);
    }

    @Override
    public void deleteClient(Long clientId, User currentUser) {
        Company company = getCompanyFromUser(currentUser);

        // 1. Find the client securely
        Client client = clientRepository.findByIdAndCompanyId(clientId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        // 2. Delete it
        clientRepository.delete(client);
    }

    // --- HELPER METHODS ---

    /**
     * Extracts the Company object from the authenticated User.
     * This is the core of our multi-tenancy logic.
     */
    private Company getCompanyFromUser(User user) {
        if (user.getCompany() == null) {
            // This should not happen if registration is working, but it's a good safe-guard.
            throw new IllegalStateException("User is not associated with a company.");
        }
        return user.getCompany();
    }

    /**
     * Maps a Client Entity to a ClientResponse DTO.
     */
    private ClientResponse clientToResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .clientName(client.getClientName())
                .clientAddress(client.getClientAddress())
                .gstinNo(client.getGstinNo())
                .state(client.getState())
                .code(client.getCode())
                .companyId(client.getCompany().getId())
                .build();
    }
}