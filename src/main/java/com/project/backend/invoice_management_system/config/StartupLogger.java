package com.project.backend.invoice_management_system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
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
    }
}

