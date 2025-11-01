package com.project.backend.invoice_management_system.common.util;

import com.project.backend.invoice_management_system.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.OffsetDateTime;
import java.util.Map;

public class ResponseBuilder {

	public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message, HttpStatus status, String path) {
		ApiResponse<T> body = ApiResponse.<T>builder()
				.success(true)
				.message(message)
				.status(status.value())
				.path(path)
				.timestamp(OffsetDateTime.now())
				.data(data)
				.build();
		return new ResponseEntity<>(body, status);
	}

	public static <T> ResponseEntity<ApiResponse<T>> ok(T data, String path) {
		return success(data, "OK", HttpStatus.OK, path);
	}

	public static <T> ResponseEntity<ApiResponse<T>> created(T data, String path) {
		return success(data, "Created", HttpStatus.CREATED, path);
	}

	public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
		return success(data, "OK", HttpStatus.OK, currentPath());
	}

	public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
		return success(data, "Created", HttpStatus.CREATED, currentPath());
	}

	public static ResponseEntity<ApiResponse<Object>> error(String message, HttpStatus status, String path) {
		ApiResponse<Object> body = ApiResponse.builder()
				.success(false)
				.message(message)
				.status(status.value())
				.path(path)
				.timestamp(OffsetDateTime.now())
				.build();
		return new ResponseEntity<>(body, status);
	}

	public static ResponseEntity<ApiResponse<Object>> validationError(Map<String, String> errors, String message, HttpStatus status, String path) {
		ApiResponse<Object> body = ApiResponse.builder()
				.success(false)
				.message(message)
				.status(status.value())
				.path(path)
				.timestamp(OffsetDateTime.now())
				.errors(errors)
				.build();
		return new ResponseEntity<>(body, status);
	}

	private static String currentPath() {
		try {
			return ServletUriComponentsBuilder.fromCurrentRequest().build().getPath();
		} catch (Exception ignored) {
			return null;
		}
	}
}