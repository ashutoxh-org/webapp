package com.csye6225.cloud.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Send BAD REQUEST if GET request has body/params
 */
@Component
public class HttpMethodValidationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpMethodValidationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if ("GET".equals(request.getMethod()) &&
                !(request.getRequestURI().contains("/user/verify")) &&
                        (request.getContentLength() > 0 || request.getQueryString() != null)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request");
            LOGGER.error("Bad GET request due to body, query params or header being present");
            return;
        }
        if (("PUT".equals(request.getMethod()) || "POST".equals(request.getMethod())) && request.getQueryString() != null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request");
            LOGGER.error("Bad PUT/POST request due to query params being present");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
