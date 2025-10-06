package com.gp9.carte_MS.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Map<String, Object> healthCheck() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "carte-service");
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now().toString());
        status.put("version", "1.0");
        
        return status;
    }
    
    @GetMapping("/debug")
    public Map<String, Object> debug() {
        Map<String, Object> debug = new HashMap<>();
        debug.put("processors", Runtime.getRuntime().availableProcessors());
        debug.put("freeMemory", Runtime.getRuntime().freeMemory());
        debug.put("maxMemory", Runtime.getRuntime().maxMemory());
        debug.put("totalMemory", Runtime.getRuntime().totalMemory());
        return debug;
    }
}
