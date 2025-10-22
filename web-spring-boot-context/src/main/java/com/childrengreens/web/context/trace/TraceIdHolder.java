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

package com.childrengreens.web.context.trace;

import org.slf4j.MDC;

/**
 * Helper around {@link MDC} that stores the current trace id.
 */
public final class TraceIdHolder {

    private static final String DEFAULT_KEY = "traceId";

    private TraceIdHolder() {
    }

    public static void set(String traceId) {
        if (traceId != null) {
            MDC.put(DEFAULT_KEY, traceId);
        }
    }

    public static String get() {
        return MDC.get(DEFAULT_KEY);
    }

    public static void clear() {
        MDC.remove(DEFAULT_KEY);
    }
}
