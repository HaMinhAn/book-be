package com.example.candy.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        String queryString = httpRequest.getQueryString();
        logger.info("REQUEST: {} {} {}", method, path, queryString != null ? "?" + queryString : "");
        java.util.Enumeration<String> headerNames = httpRequest.getHeaderNames();
        if (headerNames != null) {
            StringBuilder headers = new StringBuilder();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.append(headerName).append("=").append(httpRequest.getHeader(headerName)).append(", ");
            }
            logger.debug("Request headers: {}", headers.toString());
        }
        try {
            chain.doFilter(request, response);
        } finally {
            logger.info("RESPONSE: {} {} -> Status: {}", method, path, httpResponse.getStatus());
        }
    }
}
