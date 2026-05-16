package com.project.backend.invoice_management_system.order.service;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.client.model.Client;
import com.project.backend.invoice_management_system.client.repository.ClientRepository;
import com.project.backend.invoice_management_system.common.exception.ResourceNotFoundException;
import com.project.backend.invoice_management_system.company.model.Company;
import com.project.backend.invoice_management_system.order.dto.OrderRequest;
import com.project.backend.invoice_management_system.order.dto.OrderResponse;
import com.project.backend.invoice_management_system.order.model.Order;
import com.project.backend.invoice_management_system.order.model.OrderItem;
import com.project.backend.invoice_management_system.order.repository.OrderRepository;
import com.project.backend.invoice_management_system.product.model.Product;
import com.project.backend.invoice_management_system.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request, User currentUser) {
        Company company = currentUser.getCompany();

        Client client = clientRepository.findByIdAndCompanyId(request.getClientId(), company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.getClientId()));

        Order order = Order.builder()
                .orderNumber("ORD-" + (1000 + new Random().nextInt(9000))) // Generates random ORD-XXXX
                .orderDate(request.getOrderDate())
                .status(request.getStatus() != null ? request.getStatus() : "Pending")
                .client(client)
                .company(company)
                .build();

        for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findByIdAndCompanyId(itemReq.getProductId(), company.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemReq.getProductId()));

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .build();

            order.addItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getOrdersByClient(Long clientId, User currentUser) {
        Company company = currentUser.getCompany();
        return orderRepository.findByClientIdAndCompanyId(clientId, company.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId, User currentUser) {
        Company company = currentUser.getCompany();
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getCompany().getId().equals(company.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        orderRepository.delete(order);
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderResponse.OrderItemDto> items = order.getItems().stream().map(item ->
                OrderResponse.OrderItemDto.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getProductName())
                        .uom(item.getProduct().getUom())
                        .quantity(item.getQuantity())
                        .build()
        ).collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .clientId(order.getClient().getId())
                .items(items)
                .build();
    }
}