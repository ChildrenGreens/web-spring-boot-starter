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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Locale;

import com.childrengreens.web.context.advice.ResponseWrappingAdvice;
import com.childrengreens.web.context.exception.GlobalExceptionHandler;
import com.childrengreens.web.context.response.ApiResponseFactory;
import com.childrengreens.web.context.trace.TraceIdFilter;
import com.childrengreens.web.context.logging.RequestLoggingFilter;
import com.childrengreens.web.context.auth.LoginRequirementEvaluator;
import com.childrengreens.web.context.auth.LoginRequiredInterceptor;
import com.childrengreens.web.context.i18n.MessageResolver;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.i18n.LocaleContextHolder;
import tools.jackson.databind.json.JsonMapper;

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
            JsonMapper.Builder builder = JsonMapper.builder();
            context.getBeanProvider(JsonMapperBuilderCustomizer.class)
                    .orderedStream().forEach((customizer) -> customizer.customize(builder));
            JsonMapper mapper = builder.build();
            DateFormat dateFormat = mapper.serializationConfig().getDateFormat();
            assertThat(dateFormat).isInstanceOf(SimpleDateFormat.class);
            assertThat(((SimpleDateFormat) dateFormat).toPattern()).isEqualTo("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            assertThat(dateFormat.getTimeZone().getID()).isEqualTo("UTC");
            String serialized = mapper.writeValueAsString(LocalDateTime.of(2025, 1, 2, 3, 4, 5));
            assertThat(serialized).isEqualTo("\"2025-01-02T03:04:05\"");
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

    @Test
    void shouldRegisterLoginInterceptorWhenEvaluatorPresent() {
        this.contextRunner.withPropertyValues("web.starter.auth.enabled=true")
                .withBean(LoginRequirementEvaluator.class, () -> (request, handler, scope) -> { })
                .run((context) -> {
                    assertThat(context).hasSingleBean(LoginRequiredInterceptor.class);
                });
    }

    @Test
    void shouldProvideMessageResolverForLocales() {
        this.contextRunner.withPropertyValues("web.starter.i18n.base-names=classpath:i18n/messages").run((context) -> {
            Locale previous = LocaleContextHolder.getLocale();
            LocaleContextHolder.setLocale(Locale.US);
            try {
                MessageResolver resolver = context.getBean(MessageResolver.class);
                assertThat(resolver.getMessageForLocale("welcome.message", Locale.CHINA)).isEqualTo("你好");
                assertThat(resolver.getMessage("welcome.message")).isEqualTo("Hello");
            }
            finally {
                LocaleContextHolder.setLocale(previous);
            }
        });
    }
}
