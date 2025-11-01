package com.project.backend.invoice_management_system.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CompanyAlreadyExistsException extends RuntimeException {

    public CompanyAlreadyExistsException() {
        super("Company profile already exists for this user");
    }

    public CompanyAlreadyExistsException(String message) {
        super(message);
    }
}


