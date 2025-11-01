package com.project.backend.invoice_management_system.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
	private boolean success;
	private String message;
	private Integer status;
	private String path;
	private OffsetDateTime timestamp;
	private T data;
	private Map<String, String> errors; // field -> message for validation or error details
}
