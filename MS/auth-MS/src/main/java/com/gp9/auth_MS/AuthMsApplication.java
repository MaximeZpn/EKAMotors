package com.gp9.auth_MS;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClients
public class AuthMsApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthMsApplication.class, args);
    }
}
