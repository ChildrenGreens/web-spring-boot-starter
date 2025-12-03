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
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * Runtime hints required for Spring AOT / native images.
 */
class WebRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(ApiResponse.class, (hint) -> hint.withMembers(MemberCategory.ACCESS_DECLARED_FIELDS,
                MemberCategory.INVOKE_PUBLIC_METHODS));
        hints.reflection().registerType(DefaultErrorCode.class,
                (hint) -> hint.withMembers(MemberCategory.ACCESS_DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_METHODS));
        hints.reflection().registerType(BusinessException.class,
                (hint) -> hint.withMembers(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                        MemberCategory.INVOKE_PUBLIC_METHODS));
        hints.reflection().registerType(UnauthorizedException.class,
                (hint) -> hint.withMembers(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                        MemberCategory.INVOKE_PUBLIC_METHODS));
    }

}
