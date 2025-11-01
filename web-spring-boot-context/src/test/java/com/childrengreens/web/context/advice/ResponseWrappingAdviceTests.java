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
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseWrappingAdviceTests {

    private final ResponseWrappingAdvice advice = new ResponseWrappingAdvice(new ApiResponseFactory());

    @Test
    void supportsReturnsTrueForJacksonConverters() throws NoSuchMethodException {
        MethodParameter parameter = methodParameter("jsonBody");

        boolean supported = this.advice.supports(parameter, MappingJackson2HttpMessageConverter.class);

        assertThat(supported).isTrue();
    }

    @Test
    void supportsReturnsFalseForBinaryConverters() throws NoSuchMethodException {
        MethodParameter parameter = methodParameter("binaryBody");

        boolean supported = this.advice.supports(parameter, ByteArrayHttpMessageConverter.class);

        assertThat(supported).isFalse();
    }

    @Test
    void beforeBodyWriteWrapsPlainObjectsWhenSupported() throws NoSuchMethodException {
        MethodParameter parameter = methodParameter("jsonBody");

        Object result = this.advice.beforeBodyWrite(new SampleBody(), parameter, null,
                MappingJackson2HttpMessageConverter.class, null, null);

        assertThat(result).isInstanceOf(ApiResponse.class);
    }

    private MethodParameter methodParameter(String methodName) throws NoSuchMethodException {
        Method method = SampleController.class.getDeclaredMethod(methodName);
        return new MethodParameter(method, -1);
    }

    private static final class SampleController {

        SampleBody jsonBody() {
            return new SampleBody();
        }

        byte[] binaryBody() {
            return new byte[0];
        }
    }

    private static final class SampleBody {
    }
}
