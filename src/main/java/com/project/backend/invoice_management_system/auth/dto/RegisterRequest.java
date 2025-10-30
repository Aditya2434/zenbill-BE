//package com.project.backend.invoice_management_system.auth.dto;
//
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class RegisterRequest {
//
//    @NotBlank(message = "Email is required")
//    @Email(message = "Email must be a valid email address")
//    private String email;
//
//    @NotBlank(message = "Password is required")
//    @Size(min = 8, message = "Password must be at least 8 characters long")
//    private String password;
//
//    @NotBlank(message = "Company name is required")
//    @Size(min = 2, message = "Company name must be at least 2 characters long")
//    private String companyName;
//
//    // We can add more fields here later, like invoicePrefix, address, etc.
//}

package com.project.backend.invoice_management_system.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Company name is required")
    @Size(min = 2, message = "Company name must be at least 2 characters long")
    private String companyName;

    // --- ADD THESE TWO FIELDS ---
    @NotBlank(message = "Company state is required")
    private String state;

    @NotBlank(message = "Company state code is required")
    private String code; // This is the state code, e.g., "19"
}