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

import net.logstash.logback.argument.StructuredArguments;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
            throw ex;
        } finally {

            long duration = System.currentTimeMillis() - startTime;

            String responseBody = new String(
                    wrappedResponse.getContentAsByteArray(),
                    StandardCharsets.UTF_8
            );

            int status = wrappedResponse.getStatus();

            // Detectar errores HTTP sin excepción
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
                    errorMessage = "HTTP Error " + status;
                }
            }

            // Construir headers 
            Map<String, String> headersMap = new HashMap<>();

            Collections.list(request.getHeaderNames()).forEach(headerName -> {
                if (!headerName.equalsIgnoreCase("authorization")) {
                    headersMap.put(headerName, request.getHeader(headerName));
                }
            });

            // LOG ESTRUCTURADO (JSON automático)
            if (status >= 500) {
                log.error("HTTP_REQUEST",
                        StructuredArguments.keyValue("level", "error"),
                        StructuredArguments.keyValue("response", "error"),
                        StructuredArguments.keyValue("codigo", status),
                        StructuredArguments.keyValue("method", request.getMethod()),
                        StructuredArguments.keyValue("path", request.getRequestURI()),
                        StructuredArguments.keyValue("durationMs", duration),
                        StructuredArguments.keyValue("responseBody", responseBody),
                        StructuredArguments.keyValue("errorMessage", errorMessage),
                        StructuredArguments.keyValue("headers", headersMap)
                );
            } else if (status >= 400) {
                log.warn("HTTP_REQUEST",
                        StructuredArguments.keyValue("level", "error"),
                        StructuredArguments.keyValue("response", "error"),
                        StructuredArguments.keyValue("codigo", status),
                        StructuredArguments.keyValue("method", request.getMethod()),
                        StructuredArguments.keyValue("path", request.getRequestURI()),
                        StructuredArguments.keyValue("durationMs", duration),
                        StructuredArguments.keyValue("responseBody", responseBody),
                        StructuredArguments.keyValue("errorMessage", errorMessage),
                        StructuredArguments.keyValue("headers", headersMap)
                );
            } else {
                log.info("HTTP_REQUEST",
                        StructuredArguments.keyValue("level", "info"),
                        StructuredArguments.keyValue("response", "respuesta exitosa"),
                        StructuredArguments.keyValue("codigo", status),
                        StructuredArguments.keyValue("method", request.getMethod()),
                        StructuredArguments.keyValue("path", request.getRequestURI()),
                        StructuredArguments.keyValue("durationMs", duration),
                        StructuredArguments.keyValue("responseBody", responseBody),
                        StructuredArguments.keyValue("errorMessage", errorMessage),
                        StructuredArguments.keyValue("headers", headersMap)
                );
            }

            wrappedResponse.copyBodyToResponse();
        }
    }
}
