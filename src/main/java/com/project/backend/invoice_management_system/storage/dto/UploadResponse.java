package com.project.backend.invoice_management_system.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    private String bucket;
    private String path; // path inside the bucket
    private String url;  // signed or public URL for immediate use
}


