package com.gp9.atelier2.config.security;

import com.gp9.atelier2.filter.JwtAuthenticationFilter;
import com.gp9.atelier2.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(AuthService authService, PasswordEncoder passwordEncoder, JwtAuthenticationFilter jwtFilter) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Ressources publiques
                        .requestMatchers("/api/auth/**").permitAll()  // Restaurer l'accès à tous les endpoints d'authentification
                        .requestMatchers("/", "/*.html", "/favicon.ico").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/", "/style/**", "/webjars/", "/assets/").permitAll()
                        .requestMatchers("/api/test-roles/public").permitAll()
                        
                        // Restrictions pour la page compte - accessible uniquement aux administrateurs
                        // Page intermédiaire pour la vérification admin
                        .requestMatchers("/admin.html").authenticated()
                        .requestMatchers("/js/admin-redirect.js").authenticated()
                        
                        // Ressources CSS/JS communes
                        .requestMatchers("/css/**", "/images/", "/style/**", "/webjars/", "/assets/").permitAll()
                        .requestMatchers("/js/auth.js", "/js/home.js").permitAll()
                        
                        // Page de test
                        .requestMatchers("/api/test-roles/public").permitAll()
                        .requestMatchers("/test-roles.html").permitAll()
                        
                        // Restrictions pour la page compte - accessible uniquement aux administrateurs
                        .requestMatchers("/compte.html").hasRole("ADMIN")
                        .requestMatchers("/style/compte.css").hasRole("ADMIN")
                        .requestMatchers("/js/compte.js").hasRole("ADMIN")
                        
                        // Pages HTML accessibles à tous les utilisateurs authentifiés
                        .requestMatchers("/home.html", "/market.html", "/sell.html", "/transaction.html", "/buy.html").authenticated()
                        
                        // APIs avec restriction par rôle
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/market/admin/**").hasRole("ADMIN")
                        .requestMatchers("/market/vente").hasAnyRole("USER", "VENDEUR")
                        .requestMatchers("/api/utilisateurs/admin/**").hasRole("ADMIN")
                        
                        // Toutes les autres requêtes nécessitent une authentification
                        .anyRequest().authenticated()
                )
                // Forcer HTTPS pour toutes les requêtes
                .requiresChannel(channel -> channel
                        .anyRequest().requiresSecure())
                
                // Améliorer la gestion des exceptions pour mieux tracer les problèmes
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Rediriger vers la page de connexion si non authentifié
                            response.sendRedirect("/connexion.html");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // Journaliser l'accès refusé pour le débogage
                            System.out.println("Accès refusé: " + request.getRequestURI() + " - Exception: " + accessDeniedException.getMessage());
                            
                            if (request.getRequestURI().contains("/compte.html") || 
                                request.getRequestURI().contains("/admin/") || 
                                request.getRequestURI().contains("/style/compte.css") || 
                                request.getRequestURI().contains("/js/compte.js")) {
                                response.sendRedirect("/connexion.html?erreur=acces_refuse");
                            } else {
                                response.setStatus(403);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\":\"Accès refusé\"}");
                            }
                        })
                );
        
        // Ajoutons un log de démarrage pour confirmer les règles d'accès
        System.out.println("Configuration de sécurité initialisée - Règles pour compte.html: hasRole('ADMIN')");
        
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(authService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
