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
package com.childrengreens.web.context.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class BusinessExceptionTests {

    @Test
    void shouldRejectNullErrorCode() {
        assertThatNullPointerException().isThrownBy(() -> new BusinessException(null));
    }

    @Test
    void shouldExposeErrorCodeAndMessage() {
        BusinessException exception = new BusinessException(DefaultErrorCode.INTERNAL_ERROR, "oops");

        assertThat(exception.getErrorCode()).isEqualTo(DefaultErrorCode.INTERNAL_ERROR);
        assertThat(exception).hasMessage("oops");
    }

    @Test
    void shouldKeepCause() {
        IllegalStateException cause = new IllegalStateException("root");
        BusinessException exception = new BusinessException(DefaultErrorCode.SUCCESS, "ok", cause);

        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getMessage()).isEqualTo("ok");
    }
}
