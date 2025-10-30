package com.project.backend.invoice_management_system.auth.model;

/**
 * Defines the roles a user can have within the application.
 */
public enum Roles {
    /**
     * Standard user with access to their own company's data
     * (invoices, clients, etc.).
     */
    ROLE_USER,

    /**
     * An administrator with elevated privileges (e.g., managing users).
     */
    ROLE_ADMIN
}