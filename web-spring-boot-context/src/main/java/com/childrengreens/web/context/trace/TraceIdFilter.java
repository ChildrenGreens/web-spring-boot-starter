/*
 *  * Copyright @inceptionYear@-@currentYear@ the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */
package com.childrengreens.web.context.trace;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that enriches each request with a trace identifier.
 */
public class TraceIdFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TraceIdFilter.class);

    private final TraceIdGenerator traceIdGenerator;

    private final String headerName;

    public TraceIdFilter(TraceIdGenerator traceIdGenerator, String headerName) {
        this.traceIdGenerator = traceIdGenerator;
        this.headerName = headerName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = resolveTraceId(request);
        TraceIdHolder.set(traceId);
        response.setHeader(this.headerName, traceId);
        try {
            filterChain.doFilter(request, response);
        }
        finally {
            TraceIdHolder.clear();
        }
    }

    private String resolveTraceId(HttpServletRequest request) {
        String headerTraceId = request.getHeader(this.headerName);
        if (StringUtils.hasText(headerTraceId)) {
            return headerTraceId;
        }
        String generated = this.traceIdGenerator.generate();
        if (log.isDebugEnabled()) {
            log.debug("Generated trace id {} for request {} {}", generated, request.getMethod(), request.getRequestURI());
        }
        return generated;
    }
}
