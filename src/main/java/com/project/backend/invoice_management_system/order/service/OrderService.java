package com.project.backend.invoice_management_system.order.service;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.order.dto.OrderRequest;
import com.project.backend.invoice_management_system.order.dto.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request, User currentUser);
    List<OrderResponse> getOrdersByClient(Long clientId, User currentUser);
    void deleteOrder(Long orderId, User currentUser);
}