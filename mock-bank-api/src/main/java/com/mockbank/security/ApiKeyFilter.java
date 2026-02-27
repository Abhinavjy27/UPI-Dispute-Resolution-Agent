package com.mockbank.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockbank.dto.ApiResponse;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * API Key Authentication Filter
 * Validates x-api-key header for all /bank endpoints
 */
@Component
@Slf4j
public class ApiKeyFilter implements Filter {

    @Value("${app.api-key}")
    private String validApiKey;

    private static final String API_KEY_HEADER = "x-api-key";
    private static final String BANK_API_PATH = "/bank";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestPath = httpRequest.getRequestURI();

        // Check if the request is for bank API endpoint
        if (requestPath.startsWith(BANK_API_PATH) && !requestPath.startsWith("/bank/h2-console")) {
            String apiKey = httpRequest.getHeader(API_KEY_HEADER);

            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.warn("Request without API key: {}", requestPath);
                sendUnauthorizedResponse(httpResponse, "Missing API key in header: " + API_KEY_HEADER);
                return;
            }

            if (!apiKey.equals(validApiKey)) {
                log.warn("Request with invalid API key: {}", requestPath);
                sendUnauthorizedResponse(httpResponse, "Invalid API key");
                return;
            }

            log.debug("Valid API key provided for request: {}", requestPath);
        }

        chain.doFilter(request, response);
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ApiResponse<Object> apiResponse = new ApiResponse<>(
                false,
                "An error occurred: " + message,
                null
        );

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(apiResponse));
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        log.info("API Key Filter initialized");
    }

    @Override
    public void destroy() {
        log.info("API Key Filter destroyed");
    }
}
