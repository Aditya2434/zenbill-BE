package com.project.backend.invoice_management_system.auth.repository;

import com.project.backend.invoice_management_system.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * This is critical for Spring Security to load the user.
     */
    Optional<User> findByEmail(String email);
}