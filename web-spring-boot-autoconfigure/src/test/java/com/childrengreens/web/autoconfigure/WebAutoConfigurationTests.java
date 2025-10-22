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

import com.childrengreens.web.context.advice.ResponseWrappingAdvice;
import com.childrengreens.web.context.exception.GlobalExceptionHandler;
import com.childrengreens.web.context.response.ApiResponseFactory;
import com.childrengreens.web.context.trace.TraceIdFilter;
import com.childrengreens.web.context.logging.RequestLoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class WebAutoConfigurationTests {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(WebAutoConfiguration.class));

    @Test
    void shouldRegisterCoreBeansByDefault() {
        this.contextRunner.run((context) -> {
            assertThat(context).hasSingleBean(ApiResponseFactory.class);
            assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
            assertThat(context).hasSingleBean(ResponseWrappingAdvice.class);
            assertThat(context.getBeansOfType(FilterRegistrationBean.class).values()).anySatisfy((bean) -> {
                assertThat(bean.getFilter()).isInstanceOf(TraceIdFilter.class);
            });
        });
    }

    @Test
    void shouldDisableResponseWrappingWhenConfigured() {
        this.contextRunner.withPropertyValues("web.starter.response.enabled=false").run((context) -> {
            assertThat(context).doesNotHaveBean(ResponseWrappingAdvice.class);
        });
    }

    @Test
    void shouldApplyJacksonCustomisations() {
        this.contextRunner.run((context) -> {
            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            context.getBeanProvider(org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer.class)
                    .orderedStream().forEach((customizer) -> customizer.customize(builder));
            ObjectMapper mapper = builder.build();
            assertThat(mapper.getRegisteredModuleIds()).isNotEmpty();
            assertThat(mapper.getSerializationConfig().isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)).isFalse();
        });
    }

    @Test
    void shouldDisableTraceFilterWhenConfigured() {
        this.contextRunner.withPropertyValues("web.starter.trace.enabled=false").run((context) -> {
            assertThat(context.getBeansOfType(FilterRegistrationBean.class).values()).noneMatch((bean) ->
                    bean.getFilter() instanceof TraceIdFilter);
        });
    }

    @Test
    void shouldDisableLoggingFilterWhenConfigured() {
        this.contextRunner.withPropertyValues("web.starter.logging.enabled=false").run((context) -> {
            assertThat(context.getBeansOfType(FilterRegistrationBean.class).values()).noneMatch((registration) ->
                    registration.getFilter() instanceof RequestLoggingFilter);
        });
    }
}

