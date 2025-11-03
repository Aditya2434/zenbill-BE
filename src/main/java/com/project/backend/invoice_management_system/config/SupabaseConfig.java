package com.project.backend.invoice_management_system.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class SupabaseConfig {

    @Value("${supabase.url:${SUPABASE_URL:}}")
    private String supabaseUrl;

    @Value("${supabase.service_role_key:${SUPABASE_SERVICE_ROLE_KEY:}}")
    private String serviceRoleKey;

    @Value("${supabase.default_bucket:document}")
    private String defaultBucket;
}


