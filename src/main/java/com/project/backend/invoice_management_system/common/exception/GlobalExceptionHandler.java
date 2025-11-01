package com.project.backend.invoice_management_system.common.exception;

import com.project.backend.invoice_management_system.common.util.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.transaction.TransactionSystemException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("404 at {}: {}", request.getRequestURI(), ex.getMessage());
		return ResponseBuilder.error(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
	}

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<?> handleInvalidRequest(InvalidRequestException ex, HttpServletRequest request) {
        log.debug("400 at {}: {}", request.getRequestURI(), ex.getMessage());
		return ResponseBuilder.error(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.debug("Validation failed at {}", request.getRequestURI());
		Map<String, String> errors = new HashMap<>();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return ResponseBuilder.validationError(errors, "Validation failed", HttpStatus.BAD_REQUEST, request.getRequestURI());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        log.debug("Constraint violation at {}", request.getRequestURI());
		Map<String, String> errors = new HashMap<>();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			String field = violation.getPropertyPath() != null ? violation.getPropertyPath().toString() : "";
			errors.put(field, violation.getMessage());
		}
		return ResponseBuilder.validationError(errors, "Validation failed", HttpStatus.BAD_REQUEST, request.getRequestURI());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<?> handleMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.debug("Malformed JSON at {}", request.getRequestURI());
		return ResponseBuilder.error("Malformed JSON request", HttpStatus.BAD_REQUEST, request.getRequestURI());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.debug("Type mismatch at {} for param {}", request.getRequestURI(), ex.getName());
		return ResponseBuilder.error("Invalid parameter: " + ex.getName(), HttpStatus.BAD_REQUEST, request.getRequestURI());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Unauthorized at {}", request.getRequestURI());
		return ResponseBuilder.error("Invalid username or password", HttpStatus.UNAUTHORIZED, request.getRequestURI());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Forbidden at {}", request.getRequestURI());
		return ResponseBuilder.error("Access denied", HttpStatus.FORBIDDEN, request.getRequestURI());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Conflict at {}: {}", request.getRequestURI(), ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());
		return ResponseBuilder.error("Data integrity violation", HttpStatus.CONFLICT, request.getRequestURI());
	}

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<?> handleEmailExists(EmailAlreadyExistsException ex, HttpServletRequest request) {
		log.warn("Email already exists at {}", request.getRequestURI());
		return ResponseBuilder.error(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI());
	}

	@ExceptionHandler(CompanyAlreadyExistsException.class)
	public ResponseEntity<?> handleCompanyExists(CompanyAlreadyExistsException ex, HttpServletRequest request) {
		log.warn("Company already exists at {}", request.getRequestURI());
		return ResponseBuilder.error(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI());
	}

    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    public ResponseEntity<?> handleHibernateConstraint(org.hibernate.exception.ConstraintViolationException ex, HttpServletRequest request) {
		log.warn("Hibernate constraint violation at {}: {}", request.getRequestURI(), ex.getConstraintName());
		String message = ex.getConstraintName() != null && ex.getConstraintName().toLowerCase().contains("email")
				? "Email already in use"
				: "Data integrity violation";
		return ResponseBuilder.error(message, HttpStatus.CONFLICT, request.getRequestURI());
	}

	@ExceptionHandler(TransactionSystemException.class)
	public ResponseEntity<?> handleTransaction(TransactionSystemException ex, HttpServletRequest request) {
		log.warn("Transaction exception at {}", request.getRequestURI());
		return ResponseBuilder.error("Data integrity violation", HttpStatus.CONFLICT, request.getRequestURI());
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Method not allowed at {}", request.getRequestURI());
		return ResponseBuilder.error("Method not allowed", HttpStatus.METHOD_NOT_ALLOWED, request.getRequestURI());
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<?> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        log.warn("No handler found for {}", request.getRequestURI());
		return ResponseBuilder.error("Resource not found", HttpStatus.NOT_FOUND, request.getRequestURI());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleAll(Exception ex, HttpServletRequest request) {
        log.error("Unhandled error at {}", request.getRequestURI(), ex);
		return ResponseBuilder.error("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
	}
}