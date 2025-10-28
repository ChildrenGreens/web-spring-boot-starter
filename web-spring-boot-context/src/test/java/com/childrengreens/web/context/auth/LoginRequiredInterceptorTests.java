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

package com.childrengreens.web.context.auth;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequiredInterceptorTests {

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        this.request = new MockHttpServletRequest();
        this.response = new MockHttpServletResponse();
    }

    @Test
    void preHandleInvokesEvaluatorWhenMethodAnnotated() throws Exception {
        RecordingEvaluator evaluator = new RecordingEvaluator();
        LoginRequiredInterceptor interceptor = new LoginRequiredInterceptor(evaluator);
        HandlerMethod handlerMethod = handlerMethod(new MethodLevelController(), "secured");

        boolean result = interceptor.preHandle(this.request, this.response, handlerMethod);

        assertThat(result).isTrue();
        assertThat(evaluator.invocation.get()).isNotNull();
        assertThat(evaluator.invocation.get().scope()).isEqualTo("method");
    }

    @Test
    void preHandleInvokesEvaluatorForTypeLevelAnnotation() throws Exception {
        RecordingEvaluator evaluator = new RecordingEvaluator();
        LoginRequiredInterceptor interceptor = new LoginRequiredInterceptor(evaluator);
        HandlerMethod handlerMethod = handlerMethod(new TypeLevelController(), "secured");

        boolean result = interceptor.preHandle(this.request, this.response, handlerMethod);

        assertThat(result).isTrue();
        assertThat(evaluator.invocation.get()).isNotNull();
        assertThat(evaluator.invocation.get().scope()).isEqualTo("type");
    }

    @Test
    void preHandleSkipsWhenNoAnnotationPresent() throws Exception {
        RecordingEvaluator evaluator = new RecordingEvaluator();
        LoginRequiredInterceptor interceptor = new LoginRequiredInterceptor(evaluator);
        HandlerMethod handlerMethod = handlerMethod(new NoAnnotationController(), "publicEndpoint");

        boolean result = interceptor.preHandle(this.request, this.response, handlerMethod);

        assertThat(result).isTrue();
        assertThat(evaluator.invocation.get()).isNull();
    }

    @Test
    void preHandleIgnoresNonHandlerMethod() throws Exception {
        RecordingEvaluator evaluator = new RecordingEvaluator();
        LoginRequiredInterceptor interceptor = new LoginRequiredInterceptor(evaluator);

        boolean result = interceptor.preHandle(this.request, this.response, new Object());

        assertThat(result).isTrue();
        assertThat(evaluator.invocation.get()).isNull();
    }

    private HandlerMethod handlerMethod(Object bean, String methodName) throws NoSuchMethodException {
        Method method = bean.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        return new HandlerMethod(bean, method);
    }

    private static final class RecordingEvaluator implements LoginRequirementEvaluator {

        private final AtomicReference<Invocation> invocation = new AtomicReference<>();

        @Override
        public void assertAuthenticated(HttpServletRequest request, HandlerMethod handler, String scope) {
            this.invocation.set(new Invocation(request, handler, scope));
        }
    }

    private record Invocation(HttpServletRequest request, HandlerMethod handler, String scope) {
    }

    private static final class MethodLevelController {

        @LoginRequired(scope = "method")
        void secured() {
        }
    }

    @LoginRequired(scope = "type")
    private static final class TypeLevelController {

        void secured() {
        }
    }

    private static final class NoAnnotationController {

        void publicEndpoint() {
        }
    }
}
