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
package com.childrengreens.web.autoconfigure;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WebStarterPropertiesTests {

    @Test
    void shouldExposeSensibleDefaults() {
        WebStarterProperties properties = new WebStarterProperties();

        assertThat(properties.getCors().isEnabled()).isTrue();
        assertThat(properties.getTrace().isEnabled()).isTrue();
        assertThat(properties.getResponse().isWrapOnNullBody()).isTrue();
        assertThat(properties.getLogging().getMaxPayloadSize().toBytes()).isEqualTo(8192);
        assertThat(properties.getJackson().getDateFormat()).isEqualTo("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }

    @Test
    void shouldAllowMutatingCollections() {
        WebStarterProperties properties = new WebStarterProperties();

        properties.getCors().setAllowedMethods(List.of("GET", "POST"));
        properties.getAuth().setIncludePatterns(List.of("/secure/**"));
        properties.getI18n().setBaseNames(List.of("classpath:i18n/messages"));
        properties.getI18n().setDefaultLocale(Locale.CANADA_FRENCH);
        properties.getI18n().setCacheDuration(Duration.ofSeconds(5));

        assertThat(properties.getCors().getAllowedMethods()).containsExactly("GET", "POST");
        assertThat(properties.getAuth().getIncludePatterns()).containsExactly("/secure/**");
        assertThat(properties.getI18n().getBaseNames()).containsExactly("classpath:i18n/messages");
        assertThat(properties.getI18n().getDefaultLocale()).isEqualTo(Locale.CANADA_FRENCH);
        assertThat(properties.getI18n().getCacheDuration()).isEqualTo(Duration.ofSeconds(5));
    }

    @Test
    void responsePropertiesShouldBeConfigurable() {
        WebStarterProperties.Response response = new WebStarterProperties.Response();

        response.setEnabled(false);
        response.setWrapOnNullBody(false);
        response.setSuccessCode("S200");
        response.setSuccessMessage("OK");
        response.setDefaultErrorCode("E500");
        response.setDefaultErrorMessage("boom");

        assertThat(response.isEnabled()).isFalse();
        assertThat(response.isWrapOnNullBody()).isFalse();
        assertThat(response.getSuccessCode()).isEqualTo("S200");
        assertThat(response.getSuccessMessage()).isEqualTo("OK");
        assertThat(response.getDefaultErrorCode()).isEqualTo("E500");
        assertThat(response.getDefaultErrorMessage()).isEqualTo("boom");
    }
}
