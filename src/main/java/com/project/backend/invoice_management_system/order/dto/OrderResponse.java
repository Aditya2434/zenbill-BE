package com.project.backend.invoice_management_system.order.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private LocalDate orderDate;
    private String status;
    private Long clientId;
    private List<OrderItemDto> items;

    @Data
    @Builder
    public static class OrderItemDto {
        private Long productId;
        private String productName;
        private String uom;
        private Integer quantity;
    }
}