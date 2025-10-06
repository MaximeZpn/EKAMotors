package com.gp9.atelier2.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.stereotype.Component;

@Component
public class LogInterceptor implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Log avant le traitement de la requête
        logger.info("Request URL: " + request.getRequestURI() + " | IP: " + request.getRemoteAddr());
        return true; // Permet de continuer le traitement de la requête
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        // Pas de log supplémentaire après la requête ici, mais tu pourrais ajouter des infos sur la réponse si nécessaire
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        // Log après que la requête a été traitée
        logger.info("Completed Request: " + request.getRequestURI() + " | Status: " + response.getStatus());
    }
}
