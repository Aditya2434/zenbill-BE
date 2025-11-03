package com.project.backend.invoice_management_system.storage.service;

import com.project.backend.invoice_management_system.storage.dto.SignUrlResponse;

public interface SupabaseStorageService {

    /**
     * Uploads bytes to Supabase Storage using the service role.
     * @return the path inside the bucket (e.g. "company/logo/uuid.png")
     */
    String upload(String bucket, String path, byte[] bytes, String contentType);

    /**
     * Creates a signed URL for a stored object.
     */
    SignUrlResponse signUrl(String bucket, String path, int expiresInSeconds);

    /**
     * Downloads a file from Supabase Storage using the service role.
     * @return the file bytes
     */
    byte[] download(String bucket, String path);
}


