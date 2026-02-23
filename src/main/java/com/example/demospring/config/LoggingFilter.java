package com.example.demospring.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        ContentCachingRequestWrapper wrappedRequest =
                new ContentCachingRequestWrapper(request);

        ContentCachingResponseWrapper wrappedResponse =
                new ContentCachingResponseWrapper(response);

        String errorMessage = null;

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
            throw ex; // importante relanzar
        } finally {

            long duration = System.currentTimeMillis() - startTime;


            String responseBody = new String(
                    wrappedResponse.getContentAsByteArray(),
                    StandardCharsets.UTF_8);

            int status = wrappedResponse.getStatus();

            // Si no hubo excepción pero es error HTTP
            if (status >= 400) {

                Object exAttr = request.getAttribute(
                        "org.springframework.web.servlet.DispatcherServlet.EXCEPTION"
                );

                if (exAttr instanceof Exception ex) {
                    errorMessage = ex.getMessage();
                }

                if ((errorMessage == null || errorMessage.isBlank())
                        && responseBody != null
                        && !responseBody.isBlank()) {
                    errorMessage = responseBody;
                }

                if (errorMessage == null || errorMessage.isBlank()) {
                    errorMessage = "HTTP Error" + status;
                }
            }

            // Construcción headers JSON
            StringBuilder headersBuilder = new StringBuilder("{");

            Collections.list(request.getHeaderNames()).forEach(headerName -> {

                // Evitar exponer tokens sensibles
                if (!headerName.equalsIgnoreCase("authorization")) {

                    String headerValue = request.getHeader(headerName);

                    headersBuilder.append("\"")
                            .append(headerName)
                            .append("\":\"")
                            .append(headerValue)
                            .append("\",");
                }
            });

            if (headersBuilder.length() > 1 &&
                headersBuilder.charAt(headersBuilder.length() - 1) == ',') {
                headersBuilder.deleteCharAt(headersBuilder.length() - 1);
            }

            headersBuilder.append("}");


            String safeResponse = responseBody == null || responseBody.isBlank()
                    ? "null"
                    : responseBody.replace("\"", "'");

            String safeError = errorMessage == null
                    ? "null"
                    : errorMessage.replace("\"", "'");

            String logMessage = String.format(
                     "{response=\"%s\" codigo=\"%d\" method=\"%s\" path=\"%s\" durationMs=\"%d\" " +
                    "responseBody=\"%s\", errorMessage=\"%s\"}",
                    safeResponse,
                    status,
                    request.getMethod(),
                    request.getRequestURI(),
                    duration,
                    safeResponse,
                    safeError
            );

            if (status >= 400) {
                log.error(logMessage);
            } else {
                log.info(logMessage);
            }

            wrappedResponse.copyBodyToResponse();
        }
    }
}