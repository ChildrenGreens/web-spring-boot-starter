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

import java.lang.reflect.Method;

import com.childrengreens.web.context.response.ApiResponse;
import com.childrengreens.web.context.response.ApiResponseFactory;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseWrappingAdviceTests {

    private final ResponseWrappingAdvice advice = new ResponseWrappingAdvice(new ApiResponseFactory());

    @Test
    // Jackson-backed responses should be wrapped
    void supportsReturnsTrueForJacksonConverters() throws NoSuchMethodException {
        MethodParameter parameter = methodParameter("jsonBody");

        boolean supported = this.advice.supports(parameter, JacksonJsonHttpMessageConverter.class);

        assertThat(supported).isTrue();
    }

    @Test
    // Binary outputs should not be wrapped to avoid double handling
    void supportsReturnsFalseForBinaryConverters() throws NoSuchMethodException {
        MethodParameter parameter = methodParameter("binaryBody");

        boolean supported = this.advice.supports(parameter, ByteArrayHttpMessageConverter.class);

        assertThat(supported).isFalse();
    }

    @Test
    // Supported return values should be wrapped into ApiResponse
    void beforeBodyWriteWrapsPlainObjectsWhenSupported() throws NoSuchMethodException {
        MethodParameter parameter = methodParameter("jsonBody");

        Object result = this.advice.beforeBodyWrite(new SampleBody(), parameter, null,
                JacksonJsonHttpMessageConverter.class, null, null);

        assertThat(result).isInstanceOf(ApiResponse.class);
    }

    @Test
    void beforeBodyWriteSkipsApiResponseAndResponseEntity() throws NoSuchMethodException {
        MethodParameter parameter = methodParameter("jsonBody");
        ApiResponse<SampleBody> response = new ApiResponseFactory().success(new SampleBody());

        Object apiResponseResult = this.advice.beforeBodyWrite(response, parameter, MediaType.APPLICATION_JSON,
                JacksonJsonHttpMessageConverter.class, new ServletServerHttpRequest(new MockHttpServletRequest()),
                new ServletServerHttpResponse(new MockHttpServletResponse()));
        Object responseEntityResult = this.advice.beforeBodyWrite(ResponseEntity.ok(new SampleBody()), parameter,
                MediaType.APPLICATION_JSON, JacksonJsonHttpMessageConverter.class,
                new ServletServerHttpRequest(new MockHttpServletRequest()),
                new ServletServerHttpResponse(new MockHttpServletResponse()));

        assertThat(apiResponseResult).isSameAs(response);
        assertThat(responseEntityResult).isInstanceOf(ResponseEntity.class);
    }

    @Test
    void beforeBodyWriteHonorsNullBodyWhenDisabled() throws Exception {
        ResponseWrappingAdvice noWrapAdvice = new ResponseWrappingAdvice(new ApiResponseFactory(), false);
        MethodParameter parameter = methodParameter("jsonBody");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        Object result = noWrapAdvice.beforeBodyWrite(null, parameter, MediaType.APPLICATION_JSON,
                JacksonJsonHttpMessageConverter.class, new ServletServerHttpRequest(request),
                new ServletServerHttpResponse(response));

        assertThat(result).isNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private MethodParameter methodParameter(String methodName) throws NoSuchMethodException {
        Method method = SampleController.class.getDeclaredMethod(methodName);
        return new MethodParameter(method, -1);
    }

    private static final class SampleController {

    }

    private static final class SampleBody {
    }
}
