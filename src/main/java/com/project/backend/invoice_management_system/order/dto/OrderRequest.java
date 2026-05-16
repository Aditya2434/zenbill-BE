package com.project.backend.invoice_management_system.order.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderRequest {
    private Long clientId;
    private LocalDate orderDate;
    private String status;
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
    }
}