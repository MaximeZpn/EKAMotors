package com.gp9.atelier2.config;

import com.gp9.atelier2.filter.LogFilter;
import com.gp9.atelier2.interceptor.LogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LogInterceptor logInterceptor;

    @Autowired
    private LogFilter logFilter;

    /**
     * Enregistre le filtre pour logger les requêtes entrantes.
     * Ce filtre va être appliqué aux chemins définis dans addUrlPatterns.
     * @return FilterRegistrationBean pour la configuration du filtre.
     */
    @Bean
    public FilterRegistrationBean<LogFilter> loggingFilter() {
        FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(logFilter);
        registrationBean.addUrlPatterns("/api/*", "/cartes/*", "/market/*", "/utilisateurs/*"); // Ajuste selon tes besoins
        return registrationBean;
    }

    /**
     * Enregistre l'intercepteur pour logger les requêtes et les réponses.
     * Ce traitement se fait avant et après la gestion de la requête.
     * @param registry L'objet InterceptorRegistry pour enregistrer les interceptors.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/api/*", "/cartes/*", "/market/*", "/utilisateurs/*"); // Ajuste selon tes besoins
    }
}
