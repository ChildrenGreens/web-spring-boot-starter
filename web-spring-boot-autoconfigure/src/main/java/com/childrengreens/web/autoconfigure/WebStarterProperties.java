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

package com.childrengreens.web.autoconfigure;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

/**
 * Configuration properties for the web starter.
 */
@ConfigurationProperties(prefix = "web.starter")
public class WebStarterProperties {

    private final Cors cors = new Cors();

    private final Trace trace = new Trace();

    private final Logging logging = new Logging();

    private final Response response = new Response();

    private final Jackson jackson = new Jackson();

    public Cors getCors() {
        return this.cors;
    }

    public Trace getTrace() {
        return this.trace;
    }

    public Logging getLogging() {
        return this.logging;
    }

    public Response getResponse() {
        return this.response;
    }

    public Jackson getJackson() {
        return this.jackson;
    }

    public static class Cors {

        private boolean enabled = true;

        private String pathPattern = "/**";

        private List<String> allowedOrigins = new ArrayList<>(Collections.singletonList("*"));

        private List<String> allowedHeaders = new ArrayList<>(Collections.singletonList("*"));

        private List<String> allowedMethods = new ArrayList<>(Collections.singletonList("*"));

        private List<String> exposedHeaders = new ArrayList<>();

        private boolean allowCredentials;

        private long maxAge = 3600;

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getPathPattern() {
            return this.pathPattern;
        }

        public void setPathPattern(String pathPattern) {
            this.pathPattern = pathPattern;
        }

        public List<String> getAllowedOrigins() {
            return this.allowedOrigins;
        }

        public void setAllowedOrigins(List<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }

        public List<String> getAllowedHeaders() {
            return this.allowedHeaders;
        }

        public void setAllowedHeaders(List<String> allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }

        public List<String> getAllowedMethods() {
            return this.allowedMethods;
        }

        public void setAllowedMethods(List<String> allowedMethods) {
            this.allowedMethods = allowedMethods;
        }

        public List<String> getExposedHeaders() {
            return this.exposedHeaders;
        }

        public void setExposedHeaders(List<String> exposedHeaders) {
            this.exposedHeaders = exposedHeaders;
        }

        public boolean isAllowCredentials() {
            return this.allowCredentials;
        }

        public void setAllowCredentials(boolean allowCredentials) {
            this.allowCredentials = allowCredentials;
        }

        public long getMaxAge() {
            return this.maxAge;
        }

        public void setMaxAge(long maxAge) {
            this.maxAge = maxAge;
        }
    }

    public static class Trace {

        private boolean enabled = true;

        private String headerName = "X-Trace-Id";

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getHeaderName() {
            return this.headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }
    }

    public static class Logging {

        private boolean enabled = true;

        private boolean includeHeaders;

        private DataSize maxPayloadSize = DataSize.ofKilobytes(8);

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isIncludeHeaders() {
            return this.includeHeaders;
        }

        public void setIncludeHeaders(boolean includeHeaders) {
            this.includeHeaders = includeHeaders;
        }

        public DataSize getMaxPayloadSize() {
            return this.maxPayloadSize;
        }

        public void setMaxPayloadSize(DataSize maxPayloadSize) {
            this.maxPayloadSize = maxPayloadSize;
        }
    }

    public static class Response {

        private boolean enabled = true;

        private boolean wrapOnNullBody = true;

        private String successCode = "0";

        private String successMessage = "Success";

        private String defaultErrorCode = "1999";

        private String defaultErrorMessage = "Internal server error";

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isWrapOnNullBody() {
            return this.wrapOnNullBody;
        }

        public void setWrapOnNullBody(boolean wrapOnNullBody) {
            this.wrapOnNullBody = wrapOnNullBody;
        }

        public String getSuccessCode() {
            return this.successCode;
        }

        public void setSuccessCode(String successCode) {
            this.successCode = successCode;
        }

        public String getSuccessMessage() {
            return this.successMessage;
        }

        public void setSuccessMessage(String successMessage) {
            this.successMessage = successMessage;
        }

        public String getDefaultErrorCode() {
            return this.defaultErrorCode;
        }

        public void setDefaultErrorCode(String defaultErrorCode) {
            this.defaultErrorCode = defaultErrorCode;
        }

        public String getDefaultErrorMessage() {
            return this.defaultErrorMessage;
        }

        public void setDefaultErrorMessage(String defaultErrorMessage) {
            this.defaultErrorMessage = defaultErrorMessage;
        }
    }

    public static class Jackson {

        private boolean writeDatesAsTimestamps;

        private String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

        private ZoneId zoneId = ZoneId.of("UTC");

        private boolean writeLongAsString;

        public boolean isWriteDatesAsTimestamps() {
            return this.writeDatesAsTimestamps;
        }

        public void setWriteDatesAsTimestamps(boolean writeDatesAsTimestamps) {
            this.writeDatesAsTimestamps = writeDatesAsTimestamps;
        }

        public String getDateFormat() {
            return this.dateFormat;
        }

        public void setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        public ZoneId getZoneId() {
            return this.zoneId;
        }

        public void setZoneId(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        public boolean isWriteLongAsString() {
            return this.writeLongAsString;
        }

        public void setWriteLongAsString(boolean writeLongAsString) {
            this.writeLongAsString = writeLongAsString;
        }
    }
}
