package com.gp9.atelier2.filter;

import com.gp9.atelier2.service.JwtService;
import com.gp9.atelier2.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@Order(1)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthService authService;

    public JwtAuthenticationFilter(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();

        // Debug détaillé pour /compte.html
        if (requestURI.contains("/compte.html")) {
            System.out.println("DEBUG - Tentative d'accès à compte.html");
            System.out.println("DEBUG - Header Auth: " + (authHeader != null ? "Présent" : "Absent"));
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails utilisateur = authService.loadUserByUsername(email);

            // Debug très détaillé des autorités
            if (requestURI.contains("/compte.html")) {
                System.out.println("DEBUG - Utilisateur: " + email);
                System.out.println("DEBUG - Rôles bruts: " + utilisateur.getAuthorities());
                System.out.println("DEBUG - Formats des rôles: " + 
                    utilisateur.getAuthorities().stream()
                        .map(a -> "'" + a.getAuthority() + "'")
                        .collect(Collectors.joining(", ")));
                
                // Vérification explicite du rôle ADMIN
                boolean hasAdminRole = utilisateur.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                System.out.println("DEBUG - A le rôle ADMIN? " + hasAdminRole);
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(utilisateur, null, utilisateur.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
            
            if (requestURI.contains("/compte.html")) {
                System.out.println("DEBUG - Authentification mise en place dans le contexte de sécurité");
            }
        }

        filterChain.doFilter(request, response);
        
        // Debug après le traitement de la chaîne de filtres
        if (requestURI.contains("/compte.html")) {
            System.out.println("DEBUG - Fin du traitement de la requête compte.html");
            System.out.println("DEBUG - Statut de réponse: " + response.getStatus());
        }
    }
}
