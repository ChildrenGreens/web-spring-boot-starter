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

import com.childrengreens.web.context.exception.BusinessException;
import com.childrengreens.web.context.exception.DefaultErrorCode;
import com.childrengreens.web.context.exception.UnauthorizedException;
import com.childrengreens.web.context.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

import static org.assertj.core.api.Assertions.assertThat;

class WebRuntimeHintsTests {

    private final WebRuntimeHints runtimeHints = new WebRuntimeHints();

    @Test
    // ApiResponse should register reflection hints for native access
    void shouldRegisterApiResponseReflection() {
        RuntimeHints hints = new RuntimeHints();
        this.runtimeHints.registerHints(hints, getClass().getClassLoader());

        assertThat(RuntimeHintsPredicates.reflection().onType(ApiResponse.class)
                .withMemberCategories(MemberCategory.ACCESS_DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS))
                .accepts(hints);
    }

    @Test
    // Business exceptions and error codes need reflection access for AOT
    void shouldRegisterErrorTypesReflection() {
        RuntimeHints hints = new RuntimeHints();
        this.runtimeHints.registerHints(hints, getClass().getClassLoader());

        assertThat(RuntimeHintsPredicates.reflection().onType(DefaultErrorCode.class)
                .withMemberCategories(MemberCategory.ACCESS_DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS))
                .accepts(hints);
        assertThat(RuntimeHintsPredicates.reflection().onType(BusinessException.class)
                .withMemberCategories(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS))
                .accepts(hints);
        assertThat(RuntimeHintsPredicates.reflection().onType(UnauthorizedException.class)
                .withMemberCategories(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS))
                .accepts(hints);
    }

}
