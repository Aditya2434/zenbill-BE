package com.project.backend.invoice_management_system.storage.dto;

import lombok.Data;

@Data
public class SignUrlRequest {
    private String bucket;   // optional, defaults to configured default bucket
    private String path;     // required, path inside bucket
    private Integer expiresIn; // seconds, default 3600
}


