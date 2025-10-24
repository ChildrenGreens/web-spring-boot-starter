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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * {@link HandlerInterceptor} that delegates authentication decisions to a
 * configurable {@link LoginRequirementEvaluator} whenever a handler is annotated
 * with {@link LoginRequired}.
 */
public class LoginRequiredInterceptor implements HandlerInterceptor {

    private final LoginRequirementEvaluator evaluator;

    public LoginRequiredInterceptor(LoginRequirementEvaluator evaluator) {
        this.evaluator = Assert.notNull(evaluator, "evaluator must not be null");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LoginRequired requirement = resolveRequirement(handler);
        if (requirement != null && handler instanceof HandlerMethod handlerMethod) {
            this.evaluator.assertAuthenticated(request, handlerMethod, requirement.scope());
        }
        return true;
    }

    @Nullable
    private LoginRequired resolveRequirement(Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return null;
        }
        LoginRequired requirement = handlerMethod.getMethodAnnotation(LoginRequired.class);
        if (requirement != null) {
            return requirement;
        }
        return handlerMethod.getBeanType().getAnnotation(LoginRequired.class);
    }
}
