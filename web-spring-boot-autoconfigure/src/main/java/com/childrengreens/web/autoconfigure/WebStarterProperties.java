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

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

/**
 * Configuration properties for the web starter.
 */
@ConfigurationProperties(prefix = "web.starter")
public class WebStarterProperties {

    /**
     * Global Cross-Origin Resource Sharing configuration applied to Spring MVC.
     */
    private final Cors cors = new Cors();

    /**
     * Trace identifier propagation settings controlling the trace filter.
     */
    private final Trace trace = new Trace();

    /**
     * Access logging options for the request logging filter.
     */
    private final Logging logging = new Logging();

    /**
     * Response wrapping behaviour for shared API responses.
     */
    private final Response response = new Response();

    /**
     * Opinionated Jackson serialization settings.
     */
    private final Jackson jackson = new Jackson();

    /**
     * Authentication guard configuration used with @LoginRequired.
     */
    private final Auth auth = new Auth();

    /**
     * Internationalisation options for message resolution.
     */
    private final I18n i18n = new I18n();

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

    public Auth getAuth() {
        return this.auth;
    }

    public I18n getI18n() {
        return this.i18n;
    }

    /**
     * Configuration applied to CORS mappings registered by the starter.
     */
    public static class Cors {

        /**
         * Whether the starter should register CORS mappings.
         */
        private boolean enabled = true;

        /**
         * Ant-style path pattern the CORS rule applies to.
         */
        private String pathPattern = "/**";

        /**
         * Allowed origins for cross-origin requests.
         */
        private List<String> allowedOrigins = new ArrayList<>(Collections.singletonList("*"));

        /**
         * Allowed headers that clients can use during the actual request.
         */
        private List<String> allowedHeaders = new ArrayList<>(Collections.singletonList("*"));

        /**
         * HTTP methods permitted for cross-origin requests.
         */
        private List<String> allowedMethods = new ArrayList<>(Collections.singletonList("*"));

        /**
         * Response headers exposed to the browser.
         */
        private List<String> exposedHeaders = new ArrayList<>();

        /**
         * Whether user credentials are supported for cross-origin requests.
         */
        private boolean allowCredentials;

        /**
         * Cache duration for pre-flight responses in seconds.
         */
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

    /**
     * Settings for the trace id generation and propagation filter.
     */
    public static class Trace {

        /**
         * Whether the trace filter should be registered.
         */
        private boolean enabled = true;

        /**
         * HTTP header name read from and written to the request/response.
         */
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

    /**
     * Options for the request logging filter.
     */
    public static class Logging {

        /**
         * Whether the logging filter should be active.
         */
        private boolean enabled = true;

        /**
         * Whether request headers should be included in the log message.
         */
        private boolean includeHeaders;

        /**
         * Maximum number of bytes from request/response bodies logged per request.
         */
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

    /**
     * Response wrapping configuration controlling the shared API model.
     */
    public static class Response {

        /**
         * Whether the {@code ResponseBodyAdvice} wrapper is enabled.
         */
        private boolean enabled = true;

        /**
         * Whether null controller responses should be wrapped as successful results.
         */
        private boolean wrapOnNullBody = true;

        /**
         * Default code used for successful responses.
         */
        private String successCode = "0";

        /**
         * Default message used for successful responses.
         */
        private String successMessage = "Success";

        /**
         * Fallback code used for unexpected errors.
         */
        private String defaultErrorCode = "1999";

        /**
         * Fallback message used for unexpected errors.
         */
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

    /**
     * Authentication guard configuration used with {@link com.childrengreens.web.context.auth.LoginRequired}.
     */
    public static class Auth {

        private boolean enabled;

        private List<String> includePatterns = new ArrayList<>(Collections.singletonList("/**"));

        private List<String> excludePatterns = new ArrayList<>();

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getIncludePatterns() {
            return this.includePatterns;
        }

        public void setIncludePatterns(List<String> includePatterns) {
            this.includePatterns = includePatterns;
        }

        public List<String> getExcludePatterns() {
            return this.excludePatterns;
        }

        public void setExcludePatterns(List<String> excludePatterns) {
            this.excludePatterns = excludePatterns;
        }
    }

    /**
     * Internationalisation options for message resolution.
     */
    public static class I18n {

        private boolean enabled = true;

        private List<String> baseNames = new ArrayList<>(Collections.singletonList("classpath:i18n/messages"));

        private String encoding = StandardCharsets.UTF_8.name();

        private Duration cacheDuration = Duration.ofMinutes(5);

        private Locale defaultLocale = Locale.getDefault();

        private boolean useCodeAsDefaultMessage = true;

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getBaseNames() {
            return this.baseNames;
        }

        public void setBaseNames(List<String> baseNames) {
            this.baseNames = baseNames;
        }

        public String getEncoding() {
            return this.encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public Duration getCacheDuration() {
            return this.cacheDuration;
        }

        public void setCacheDuration(Duration cacheDuration) {
            this.cacheDuration = cacheDuration;
        }

        public Locale getDefaultLocale() {
            return this.defaultLocale;
        }

        public void setDefaultLocale(Locale defaultLocale) {
            this.defaultLocale = defaultLocale;
        }

        public boolean isUseCodeAsDefaultMessage() {
            return this.useCodeAsDefaultMessage;
        }

        public void setUseCodeAsDefaultMessage(boolean useCodeAsDefaultMessage) {
            this.useCodeAsDefaultMessage = useCodeAsDefaultMessage;
        }
    }

    /**
     * Jackson serialization customisations applied by the starter.
     */
    public static class Jackson {

        /**
         * Whether dates should be written as numeric timestamps.
         */
        private boolean writeDatesAsTimestamps;

        /**
         * Date format applied to {@code java.util.Date}-based types.
         */
        private String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

        /**
         * Time zone applied when formatting dates.
         */
        private ZoneId zoneId = ZoneId.of("UTC");

        /**
         * Whether {@code long} values should be serialised as strings to avoid precision loss.
         */
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
