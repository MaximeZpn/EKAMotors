package com.gp9.atelier2.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
public class RoleController {

    @GetMapping("/current-user")
    public Map<String, Object> getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> userInfo = new HashMap<>();
        
        if (authentication != null) {
            userInfo.put("authenticated", authentication.isAuthenticated());
            userInfo.put("principal", authentication.getName());
            userInfo.put("roles", authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            userInfo.put("hasAdminRole", authentication.getAuthorities()
                    .stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        } else {
            userInfo.put("authenticated", false);
        }
        
        return userInfo;
    }
    
    @GetMapping("/check-admin-access")
    public Map<String, Object> checkAdminAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> result = new HashMap<>();
        
        result.put("hasAdminRole", authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
                
        return result;
    }
}
