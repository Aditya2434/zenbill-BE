package com.project.backend.invoice_management_system.storage.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.backend.invoice_management_system.config.SupabaseConfig;
import com.project.backend.invoice_management_system.storage.dto.SignUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class SupabaseStorageServiceImpl implements SupabaseStorageService {

    private final SupabaseConfig supabaseConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String upload(String bucket, String path, byte[] bytes, String contentType) {
        Assert.hasText(supabaseConfig.getSupabaseUrl(), "SUPABASE_URL not configured");
        Assert.hasText(supabaseConfig.getServiceRoleKey(), "SUPABASE_SERVICE_ROLE_KEY not configured");
        String useBucket = (bucket == null || bucket.isBlank()) ? supabaseConfig.getDefaultBucket() : bucket;
        String url = supabaseConfig.getSupabaseUrl().replaceAll("/$", "") + "/storage/v1/object/" + useBucket + "/" + path;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType != null && !contentType.isBlank() ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE));
            headers.setBearerAuth(supabaseConfig.getServiceRoleKey());
            headers.add("x-upsert", "true");

            RequestEntity<byte[]> request = new RequestEntity<>(bytes, headers, HttpMethod.POST, URI.create(url));
            ResponseEntity<String> response = restTemplate.exchange(request, String.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                String errorBody = response.getBody() != null ? response.getBody() : "No error body";
                throw new IllegalStateException("Supabase upload failed with status " + response.getStatusCode() + 
                    " for URL: " + url + ". Error: " + errorBody);
            }
            
            System.out.println("✅ Successfully uploaded to Supabase: " + path);
            return path;
        } catch (Exception e) {
            System.err.println("❌ Failed to upload to Supabase. URL: " + url + 
                ", Bucket: " + useBucket + ", Path: " + path + ". Error: " + e.getMessage());
            throw new IllegalStateException("Failed to upload to Supabase: " + e.getMessage(), e);
        }
    }

    @Override
    public SignUrlResponse signUrl(String bucket, String path, int expiresInSeconds) {
        Assert.hasText(supabaseConfig.getSupabaseUrl(), "SUPABASE_URL not configured");
        Assert.hasText(supabaseConfig.getServiceRoleKey(), "SUPABASE_SERVICE_ROLE_KEY not configured");
        String useBucket = (bucket == null || bucket.isBlank()) ? supabaseConfig.getDefaultBucket() : bucket;
        int expires = expiresInSeconds > 0 ? expiresInSeconds : 3600;
        String base = supabaseConfig.getSupabaseUrl().replaceAll("/$", "");
        String url = base + "/storage/v1/object/sign/" + useBucket + "/" + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(supabaseConfig.getServiceRoleKey());

        String body = "{\"expiresIn\":" + expires + "}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Supabase signUrl failed: " + response.getStatusCode());
        }

        try {
            JsonNode node = objectMapper.readTree(response.getBody());
            String signedPath = null;
            if (node.has("signedURL")) {
                signedPath = node.get("signedURL").asText();
            } else if (node.has("signedUrl")) {
                signedPath = node.get("signedUrl").asText();
            }
            if (signedPath == null || signedPath.isBlank()) {
                throw new IllegalStateException("Supabase signUrl missing signedURL");
            }
            String absolute;
            if (signedPath.startsWith("http://") || signedPath.startsWith("https://")) {
                absolute = signedPath;
            } else {
                String normalized = signedPath.startsWith("/") ? signedPath : "/" + signedPath;
                // Supabase returns paths like "/object/sign/..." that are relative to "/storage/v1"
                if (!normalized.startsWith("/storage/v1")) {
                    absolute = base + "/storage/v1" + normalized;
                } else {
                    absolute = base + normalized;
                }
            }
            return SignUrlResponse.builder().url(absolute).build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse Supabase signUrl response", e);
        }
    }

    @Override
    public byte[] download(String bucket, String path) {
        Assert.hasText(supabaseConfig.getSupabaseUrl(), "SUPABASE_URL not configured");
        Assert.hasText(supabaseConfig.getServiceRoleKey(), "SUPABASE_SERVICE_ROLE_KEY not configured");
        String useBucket = (bucket == null || bucket.isBlank()) ? supabaseConfig.getDefaultBucket() : bucket;
        String url = supabaseConfig.getSupabaseUrl().replaceAll("/$", "") + "/storage/v1/object/" + useBucket + "/" + path;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(supabaseConfig.getServiceRoleKey());

            RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
            ResponseEntity<byte[]> response = restTemplate.exchange(request, byte[].class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                String errorBody = response.getBody() != null ? new String(response.getBody()) : "No error body";
                throw new IllegalStateException("Supabase download failed with status " + response.getStatusCode() + 
                    " for URL: " + url + ". Error: " + errorBody);
            }
            
            if (response.getBody() == null) {
                throw new IllegalStateException("Supabase download returned null body for URL: " + url);
            }
            
            return response.getBody();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to download from Supabase. URL: " + url + 
                ", Bucket: " + useBucket + ", Path: " + path + ". Error: " + e.getMessage(), e);
        }
    }
}


