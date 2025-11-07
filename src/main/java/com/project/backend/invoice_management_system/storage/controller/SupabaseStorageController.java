package com.project.backend.invoice_management_system.storage.controller;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.common.dto.ApiResponse;
import com.project.backend.invoice_management_system.common.util.ResponseBuilder;
import com.project.backend.invoice_management_system.config.SupabaseConfig;
import com.project.backend.invoice_management_system.security.config.JwtUtils;
import com.project.backend.invoice_management_system.storage.dto.SignUrlRequest;
import com.project.backend.invoice_management_system.storage.dto.SignUrlResponse;
import com.project.backend.invoice_management_system.storage.dto.UploadResponse;
import com.project.backend.invoice_management_system.storage.service.SupabaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class SupabaseStorageController {

    private final SupabaseStorageService storageService;
    private final SupabaseConfig supabaseConfig;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UploadResponse>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false) String folder,
            @RequestParam(value = "bucket", required = false) String bucket,
            @AuthenticationPrincipal User user
    ) {
        try {
            System.out.println("========================================");
            System.out.println("📤 UPLOAD REQUEST");
            System.out.println("File: " + file.getOriginalFilename());
            System.out.println("Size: " + file.getSize() + " bytes");
            System.out.println("Content Type: " + file.getContentType());
            System.out.println("Folder: " + folder);
            System.out.println("Bucket: " + bucket);
            System.out.println("User: " + user.getEmail());
            System.out.println("========================================");
            
            String safeFolder = (folder == null || folder.isBlank()) ? "company" : folder.replaceAll("^/+|/+$", "");
            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String name = UUID.randomUUID().toString() + (ext != null && !ext.isBlank() ? ("." + ext) : "");
            String path = safeFolder + "/" + name;

            String usedBucket = (bucket == null || bucket.isBlank()) ? supabaseConfig.getDefaultBucket() : bucket;
            
            System.out.println("Uploading to: " + usedBucket + "/" + path);
            storageService.upload(usedBucket, path, file.getBytes(), file.getContentType());

            // Generate a short-lived access token for this image (embedded in URL)
            String token = jwtUtils.generateToken(user);
            
            // Return proxy URL with token in query param (works with <img> tags)
            String proxyUrl = "/api/v1/storage/image?bucket=" + usedBucket + "&path=" + path + "&token=" + token;

            UploadResponse response = UploadResponse.builder()
                    .bucket(usedBucket)
                    .path(path)
                    .url(proxyUrl)
                    .build();
            
            System.out.println("✅ Upload successful! Proxy URL: " + proxyUrl);
            return ResponseBuilder.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Upload failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    @PostMapping("/sign-url")
    public ResponseEntity<ApiResponse<SignUrlResponse>> signUrl(@RequestBody SignUrlRequest request) {
        String bucket = (request.getBucket() == null || request.getBucket().isBlank()) ? supabaseConfig.getDefaultBucket() : request.getBucket();
        int expires = request.getExpiresIn() != null ? request.getExpiresIn() : 3600;
        SignUrlResponse result = storageService.signUrl(bucket, request.getPath(), expires);
        return ResponseBuilder.ok(result);
    }

    /**
     * Secure proxy endpoint for private bucket images.
     * Uses token in query param for authentication (compatible with <img> tags).
     */
    @GetMapping("/image")
    public ResponseEntity<byte[]> getImage(
            @RequestParam(value = "bucket", required = false) String bucket,
            @RequestParam("path") String path,
            @RequestParam(value = "token", required = false) String token
    ) {
        // Validate token from query param
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(401).body(null);
        }
        
        try {
            // Extract username and validate token
            String username = jwtUtils.extractUsername(token);
            var userDetails = userDetailsService.loadUserByUsername(username);
            
            if (!jwtUtils.isTokenValid(token, userDetails)) {
                return ResponseEntity.status(401).body(null);
            }
            
            // Token is valid, proceed to fetch image
            String usedBucket = (bucket == null || bucket.isBlank()) ? supabaseConfig.getDefaultBucket() : bucket;
            byte[] imageBytes = storageService.download(usedBucket, path);
            
            // Determine content type from path extension
            String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            if (path.endsWith(".png")) contentType = MediaType.IMAGE_PNG_VALUE;
            else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) contentType = MediaType.IMAGE_JPEG_VALUE;
            else if (path.endsWith(".gif")) contentType = MediaType.IMAGE_GIF_VALUE;
            else if (path.endsWith(".webp")) contentType = "image/webp";
            else if (path.endsWith(".svg")) contentType = "image/svg+xml";
            else if (path.endsWith(".pdf")) contentType = MediaType.APPLICATION_PDF_VALUE;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .cacheControl(org.springframework.http.CacheControl.maxAge(7, java.util.concurrent.TimeUnit.DAYS))
                    .body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
