package com.project.backend.invoice_management_system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Override
    public void run(String... args) {
        System.out.println("========================================");
        System.out.println("DATABASE CONNECTION CONFIGURATION");
        System.out.println("========================================");
        System.out.println("DB URL: " + datasourceUrl);
        System.out.println("DB Username: " + datasourceUsername);
        System.out.println("Max Pool Size: " + maxPoolSize);
        System.out.println("========================================");
        
        // Check if static files exist
        System.out.println("========================================");
        System.out.println("STATIC FILES CHECK");
        System.out.println("========================================");
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:static/**");
            System.out.println("Found " + resources.length + " static files");
            for (int i = 0; i < Math.min(10, resources.length); i++) {
                System.out.println("  - " + resources[i].getFilename());
            }
        } catch (Exception e) {
            System.out.println("Error checking static files: " + e.getMessage());
        }
        System.out.println("========================================");
    }
}

