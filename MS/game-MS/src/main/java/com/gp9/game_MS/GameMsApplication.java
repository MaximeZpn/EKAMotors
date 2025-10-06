package com.gp9.game_MS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Enable scheduled tasks for energy regeneration
public class GameMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameMsApplication.class, args);
    }
}
