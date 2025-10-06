package com.gp9.carte_MS.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;  // Changed from javax.servlet.http
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class DiagnosticController {

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "carte-service");
        info.put("status", "UP");
        info.put("time", System.currentTimeMillis());
        info.put("java.version", System.getProperty("java.version"));
        info.put("memory.free", Runtime.getRuntime().freeMemory());
        info.put("memory.total", Runtime.getRuntime().totalMemory());
        
        return ResponseEntity.ok(info);
    }
    
    @GetMapping("/echo")
    public ResponseEntity<Map<String, Object>> echo(HttpServletRequest request) {
        Map<String, Object> echo = new HashMap<>();
        
        // Request details
        echo.put("path", request.getRequestURI());
        echo.put("method", request.getMethod());
        echo.put("remoteAddr", request.getRemoteAddr());
        
        // Headers
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        echo.put("headers", headers);
        
        // Query parameters
        Map<String, String[]> queryParams = request.getParameterMap();
        echo.put("queryParams", queryParams);
        
        return ResponseEntity.ok(echo);
    }
    
    @PostMapping("/echo")
    public ResponseEntity<Map<String, Object>> echoPost(
            @RequestBody(required = false) Object body,
            HttpServletRequest request) {
        Map<String, Object> echo = new HashMap<>();
        
        // Basic request info
        echo.put("path", request.getRequestURI());
        echo.put("method", request.getMethod());
        
        // Request body
        echo.put("body", body);
        
        return ResponseEntity.ok(echo);
    }
}
