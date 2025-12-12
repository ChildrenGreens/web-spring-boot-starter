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
package com.childrengreens.web.context.trace;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class TraceIdFilterTests {

    @AfterEach
    void clearTrace() {
        TraceIdHolder.clear();
    }

    @Test
    void shouldReuseIncomingTraceHeader() throws Exception {
        TraceIdGenerator generator = () -> "generated-trace";
        TraceIdFilter filter = new TraceIdFilter(generator, "X-Trace-Id");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        request.addHeader("X-Trace-Id", "incoming-trace");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean invoked = new AtomicBoolean();

        filter.doFilter(request, response, (req, res) -> invoked.set(true));

        assertThat(response.getHeader("X-Trace-Id")).isEqualTo("incoming-trace");
        assertThat(invoked).isTrue();
        assertThat(TraceIdHolder.get()).isNull();
    }

    @Test
    void shouldGenerateTraceWhenHeaderMissing() throws Exception {
        AtomicBoolean generated = new AtomicBoolean();
        TraceIdGenerator generator = () -> {
            generated.set(true);
            return "generated-trace";
        };
        TraceIdFilter filter = new TraceIdFilter(generator, "X-Trace-Id");
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/submit");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean invoked = new AtomicBoolean();

        filter.doFilter(request, response, (req, res) -> invoked.set(true));

        assertThat(generated).isTrue();
        assertThat(response.getHeader("X-Trace-Id")).isEqualTo("generated-trace");
        assertThat(invoked).isTrue();
        assertThat(TraceIdHolder.get()).isNull();
    }
}
