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

import com.childrengreens.web.context.advice.ResponseWrappingAdvice;
import com.childrengreens.web.context.auth.LoginRequiredInterceptor;
import com.childrengreens.web.context.auth.LoginRequirementEvaluator;
import com.childrengreens.web.context.exception.GlobalExceptionHandler;
import com.childrengreens.web.context.i18n.MessageResolver;
import com.childrengreens.web.context.i18n.MessageResolverImpl;
import com.childrengreens.web.context.logging.RequestLoggingFilter;
import com.childrengreens.web.context.response.ApiResponseFactory;
import com.childrengreens.web.context.trace.TraceIdFilter;
import com.childrengreens.web.context.trace.TraceIdGenerator;
import com.childrengreens.web.context.trace.UuidsTraceIdGenerator;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.TimeZone;

/**
 * Auto-configuration that exposes the web starter opinionated defaults.
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({ Jackson2ObjectMapperBuilder.class, WebMvcConfigurer.class })
@EnableConfigurationProperties(WebStarterProperties.class)
public class WebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ApiResponseFactory apiResponseFactory(WebStarterProperties properties) {
        WebStarterProperties.Response response = properties.getResponse();
        return new ApiResponseFactory(response::getSuccessCode, response::getSuccessMessage,
                response::getDefaultErrorCode, response::getDefaultErrorMessage);
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler(ApiResponseFactory responseFactory) {
        return new GlobalExceptionHandler(responseFactory);
    }

    @Bean
    @ConditionalOnProperty(prefix = "web.starter.response", name = "enabled", havingValue = "true", matchIfMissing = true)
    public ResponseWrappingAdvice responseWrappingAdvice(ApiResponseFactory responseFactory,
            WebStarterProperties properties) {
        return new ResponseWrappingAdvice(responseFactory, properties.getResponse().isWrapOnNullBody());
    }

    @Bean
    @ConditionalOnProperty(prefix = "web.starter.cors", name = "enabled", havingValue = "true", matchIfMissing = true)
    public WebMvcConfigurer webStarterCorsConfigurer(WebStarterProperties properties) {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                WebStarterProperties.Cors cors = properties.getCors();
                if (!cors.isEnabled()) {
                    return;
                }
                var mapping = registry.addMapping(cors.getPathPattern())
                        .allowedOrigins(cors.getAllowedOrigins().toArray(new String[0]))
                        .allowedMethods(cors.getAllowedMethods().toArray(new String[0]))
                        .allowedHeaders(cors.getAllowedHeaders().toArray(new String[0]))
                        .maxAge(cors.getMaxAge());
                if (!cors.getExposedHeaders().isEmpty()) {
                    mapping.exposedHeaders(cors.getExposedHeaders().toArray(new String[0]));
                }
                mapping.allowCredentials(cors.isAllowCredentials());
            }
        };
    }

    @Bean
    @ConditionalOnProperty(prefix = "web.starter.trace", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(TraceIdGenerator.class)
    public TraceIdGenerator traceIdGenerator() {
        return new UuidsTraceIdGenerator();
    }

    @Bean
    @ConditionalOnProperty(prefix = "web.starter.trace", name = "enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<TraceIdFilter> traceIdFilter(TraceIdGenerator traceIdGenerator,
            WebStarterProperties properties) {
        FilterRegistrationBean<TraceIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TraceIdFilter(traceIdGenerator, properties.getTrace().getHeaderName()));
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return registration;
    }

    @Bean(name = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)
    @ConditionalOnProperty(prefix = "web.starter.i18n", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(name = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)
    public MessageSource webStarterMessageSource(WebStarterProperties properties) {
        WebStarterProperties.I18n i18n = properties.getI18n();
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(i18n.getBaseNames().toArray(new String[0]));
        messageSource.setDefaultEncoding(i18n.getEncoding());
        messageSource.setCacheMillis(i18n.getCacheDuration().toMillis());
        messageSource.setDefaultLocale(i18n.getDefaultLocale());
        messageSource.setUseCodeAsDefaultMessage(i18n.isUseCodeAsDefaultMessage());
        return messageSource;
    }

    @Bean
    @ConditionalOnProperty(prefix = "web.starter.i18n", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(LocaleResolver.class)
    public LocaleResolver webStarterLocaleResolver(WebStarterProperties properties) {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(properties.getI18n().getDefaultLocale());
        return resolver;
    }

    @Bean
    @ConditionalOnProperty(prefix = "web.starter.i18n", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnBean(MessageSource.class)
    @ConditionalOnMissingBean(MessageResolver.class)
    public MessageResolver messageResolver(MessageSource messageSource) {
        return new MessageResolverImpl(messageSource);
    }

    @Bean
    @ConditionalOnProperty(prefix = "web.starter.auth", name = "enabled", havingValue = "true")
    @ConditionalOnBean(LoginRequirementEvaluator.class)
    public LoginRequiredInterceptor loginRequiredInterceptor(LoginRequirementEvaluator evaluator) {
        return new LoginRequiredInterceptor(evaluator);
    }

    @Bean
    @ConditionalOnProperty(prefix = "web.starter.auth", name = "enabled", havingValue = "true")
    @ConditionalOnBean(LoginRequiredInterceptor.class)
    public WebMvcConfigurer loginRequiredConfigurer(WebStarterProperties properties,
            LoginRequiredInterceptor interceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                WebStarterProperties.Auth auth = properties.getAuth();
                var registration = registry.addInterceptor(interceptor);
                if (!auth.getIncludePatterns().isEmpty()) {
                    registration.addPathPatterns(auth.getIncludePatterns().toArray(new String[0]));
                }
                if (!auth.getExcludePatterns().isEmpty()) {
                    registration.excludePathPatterns(auth.getExcludePatterns().toArray(new String[0]));
                }
            }
        };
    }

    @Bean
    @ConditionalOnProperty(prefix = "web.starter.logging", name = "enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<RequestLoggingFilter> requestLoggingFilter(WebStarterProperties properties) {
        int maxPayload = (int) Math.min(Integer.MAX_VALUE, properties.getLogging().getMaxPayloadSize().toBytes());
        RequestLoggingFilter filter = new RequestLoggingFilter(properties.getLogging().isIncludeHeaders(), maxPayload);
        FilterRegistrationBean<RequestLoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
        return registration;
    }

    @Bean
    @ConditionalOnClass(Jackson2ObjectMapperBuilder.class)
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer(WebStarterProperties properties) {
        return builder -> configureJackson(builder, properties);
    }

    private void configureJackson(Jackson2ObjectMapperBuilder builder, WebStarterProperties properties) {
        WebStarterProperties.Jackson jackson = properties.getJackson();
        builder.timeZone(TimeZone.getTimeZone(jackson.getZoneId()));
        builder.simpleDateFormat(jackson.getDateFormat());
        builder.modulesToInstall(com.fasterxml.jackson.datatype.jsr310.JavaTimeModule.class);
        if (!jackson.isWriteDatesAsTimestamps()) {
            builder.featuresToDisable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }
        if (jackson.isWriteLongAsString()) {
            builder.serializerByType(Long.class, com.fasterxml.jackson.databind.ser.std.ToStringSerializer.instance);
            builder.serializerByType(Long.TYPE, com.fasterxml.jackson.databind.ser.std.ToStringSerializer.instance);
        }
    }
}
