package com.project.backend.invoice_management_system.common.exception;

public class EmailAlreadyExistsException extends RuntimeException {

	public EmailAlreadyExistsException(String email) {
		super("Email already in use: " + email);
	}
}

