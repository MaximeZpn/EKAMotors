package com.gp9.carte_MS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@EnableScheduling
public class CarteMsApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CarteMsApplication.class, args);
    }
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            try {
                // Execute the SQL script to update existing cards
                String script = readResourceFile("db/update-cards-energy.sql");
                System.out.println("Running database initialization script...");
                
                // Split statements on semicolons
                String[] statements = script.split(";");
                for(String statement : statements) {
                    statement = statement.trim();
                    if (!statement.isEmpty()) {
                        try {
                            jdbcTemplate.execute(statement);
                        } catch (Exception e) {
                            System.err.println("Error executing statement: " + statement);
                            System.err.println("Error message: " + e.getMessage());
                        }
                    }
                }
                
                System.out.println("Database initialization completed.");
                
            } catch (IOException e) {
                System.err.println("Error reading SQL script: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error initializing database: " + e.getMessage());
            }
        };
    }
    
    private String readResourceFile(String resourcePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }
}
