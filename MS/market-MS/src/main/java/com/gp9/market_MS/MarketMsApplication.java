package com.gp9.market_MS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MarketMsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarketMsApplication.class, args);
    }
}
