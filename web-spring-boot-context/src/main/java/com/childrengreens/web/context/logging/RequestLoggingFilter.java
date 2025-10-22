/*
 * Copyright 2012-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.childrengreens.web.context.logging;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

import com.childrengreens.web.context.trace.TraceIdHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * Servlet filter that logs the lifecycle of incoming requests.
 */
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    private final boolean includeHeaders;

    private final int maxPayloadSize;

    public RequestLoggingFilter(boolean includeHeaders, int maxPayloadSize) {
        this.includeHeaders = includeHeaders;
        this.maxPayloadSize = maxPayloadSize;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        Instant start = Instant.now();
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        }
        finally {
            Instant end = Instant.now();
            Duration duration = Duration.between(start, end);
            logRequestResponse(wrappedRequest, wrappedResponse, duration);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequestResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
            Duration duration) {
        if (!log.isInfoEnabled()) {
            return;
        }
        String queryString = request.getQueryString();
        String url = request.getRequestURI() + (queryString != null ? "?" + queryString : "");
        StringBuilder message = new StringBuilder("traceId=")
                .append(TraceIdHolder.get())
                .append(' ')
                .append(request.getMethod())
                .append(' ')
                .append(url)
                .append(" status=")
                .append(response.getStatus())
                .append(" duration=")
                .append(duration.toMillis())
                .append("ms");
        if (this.includeHeaders) {
            message.append(" headers=")
                    .append(Collections.list(request.getHeaderNames()));
        }
        byte[] requestBody = request.getContentAsByteArray();
        if (requestBody.length > 0) {
            message.append(" requestBody=")
                    .append(truncatePayload(requestBody));
        }
        byte[] responseBody = response.getContentAsByteArray();
        if (responseBody.length > 0) {
            message.append(" responseBody=")
                    .append(truncatePayload(responseBody));
        }
        log.info(message.toString());
    }

    private String truncatePayload(byte[] body) {
        int length = Math.min(body.length, this.maxPayloadSize);
        String content = new String(body, 0, length, StandardCharsets.UTF_8);
        if (body.length > this.maxPayloadSize) {
            return content + "...";
        }
        return content;
    }
}
