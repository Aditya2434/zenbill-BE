package com.project.backend.invoice_management_system.auth.model;

import com.project.backend.invoice_management_system.company.model.Company;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles role;

    /**
     * This is the link to the company profile.
     * 'mappedBy = "user"' means the 'Company' entity is responsible
     * for the foreign key ('user_id' column).
     * CascadeType.ALL means if a User is deleted, their Company profile
     * is also deleted.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Company company;

    // --- UserDetails Methods ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // We return a list containing our single role
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        // Our 'username' is the email address
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // We can add logic for this later
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // We can add logic for this later
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // We can add logic for this later
    }

    @Override
    public boolean isEnabled() {
        return true; // We can add logic for this later
    }
}