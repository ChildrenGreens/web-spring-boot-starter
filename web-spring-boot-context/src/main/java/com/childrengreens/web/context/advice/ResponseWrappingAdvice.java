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
package com.childrengreens.web.context.advice;

import com.childrengreens.web.context.response.ApiResponse;
import com.childrengreens.web.context.response.ApiResponseFactory;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.AbstractJacksonHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * {@link ResponseBodyAdvice} that ensures all responses share the same layout.
 */
@ControllerAdvice
public class ResponseWrappingAdvice implements ResponseBodyAdvice<Object> {

    private final ApiResponseFactory responseFactory;

    private final boolean wrapOnNullBody;

    public ResponseWrappingAdvice(ApiResponseFactory responseFactory) {
        this(responseFactory, true);
    }

    public ResponseWrappingAdvice(ApiResponseFactory responseFactory, boolean wrapOnNullBody) {
        this.responseFactory = responseFactory;
        this.wrapOnNullBody = wrapOnNullBody;
    }

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class converterType) {
        return AbstractJacksonHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType,
                                  @NonNull Class selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        if (body instanceof ApiResponse<?> || body instanceof ResponseEntity<?> || body instanceof String) {
            return body;
        }
        if (body == null && !this.wrapOnNullBody) {
            response.setStatusCode(HttpStatus.NO_CONTENT);
            return null;
        }
        return this.responseFactory.success(body);
    }
}
