/*
 * Copyright 2012-2025 the original author or authors.
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

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import com.childrengreens.web.context.trace.TraceIdHolder;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLoggingFilterTests {

    @AfterEach
    void clearTrace() {
        TraceIdHolder.clear();
    }

    @Test
    void shouldLogAndCopyResponseBody() throws Exception {
        TraceIdHolder.set("trace-1");
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/test");
        request.setQueryString("q=1");
        request.addHeader("X-Test", "demo");
        request.setContent("request-body-content".getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestLoggingFilter filter = new RequestLoggingFilter(true, 8);
        AtomicReference<String> responseContent = new AtomicReference<>();
        FilterChain chain = (servletRequest, servletResponse) -> {
            servletResponse.setContentType("text/plain");
            servletResponse.getWriter().write("response-body-content");
            responseContent.set("response-body-content");
        };

        filter.doFilter(request, response, chain);

        assertThat(response.getContentAsString()).isEqualTo(responseContent.get());
        assertThat(TraceIdHolder.get()).isNull();
    }

    @Test
    void shouldSkipHeadersWhenDisabled() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/minimal");
        request.setContent("body".getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestLoggingFilter filter = new RequestLoggingFilter(false, 4);
        FilterChain chain = (servletRequest, servletResponse) ->
                servletResponse.getWriter().write("ok");

        filter.doFilter(request, response, chain);

        assertThat(response.getContentAsString()).isEqualTo("ok");
        assertThat(TraceIdHolder.get()).isNull();
    }
}
