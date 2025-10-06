package com.gp9.atelier2.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LogFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String url = httpRequest.getRequestURI();
            String ip = request.getRemoteAddr();  // IP du client

            // Log uniquement les informations essentielles
            logger.info("Request URL: " + url + " | IP: " + ip);
        }

        // Continuer le traitement de la requête
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Pas nécessaire ici
    }

    @Override
    public void destroy() {
        // Pas nécessaire ici
    }
}
