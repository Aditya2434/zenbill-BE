package com.project.backend.invoice_management_system.common.exception;

public class InvalidRequestException extends RuntimeException {

	public InvalidRequestException(String message) {
		super(message);
	}

	public InvalidRequestException(String message, Throwable cause) {
		super(message, cause);
	}
}