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
package com.childrengreens.web.context.response;

import com.childrengreens.web.context.exception.DefaultErrorCode;
import com.childrengreens.web.context.exception.ErrorCode;
import com.childrengreens.web.context.trace.TraceIdHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseFactoryTests {

    @AfterEach
    void clearTrace() {
        TraceIdHolder.clear();
    }

    @Test
    void successResponsesUseSuppliedDefaults() {
        ApiResponseFactory factory = new ApiResponseFactory(() -> "S-1", () -> "ok",
                () -> "E-1", () -> "fail");

        ApiResponse<String> response = factory.success("payload");

        assertThat(response.getCode()).isEqualTo("S-1");
        assertThat(response.getMessage()).isEqualTo("ok");
        assertThat(response.getData()).isEqualTo("payload");
        assertThat(response.getTraceId()).isNull();
    }

    @Test
    void failureResponsesPropagateErrorCode() {
        ErrorCode errorCode = new ErrorCode() {
            @Override
            public String getCode() {
                return "E-123";
            }

            @Override
            public String getMessage() {
                return "custom error";
            }
        };
        ApiResponseFactory factory = new ApiResponseFactory();

        ApiResponse<Void> response = factory.failure(errorCode, "override message");

        assertThat(response.getCode()).isEqualTo("E-123");
        assertThat(response.getMessage()).isEqualTo("override message");
        assertThat(response.getTraceId()).isNull();

        ApiResponse<Void> defaultFailure = factory.failure(DefaultErrorCode.INTERNAL_ERROR);
        assertThat(defaultFailure.getCode()).isEqualTo(DefaultErrorCode.INTERNAL_ERROR.getCode());
        assertThat(defaultFailure.getMessage()).isEqualTo(DefaultErrorCode.INTERNAL_ERROR.getMessage());
    }
}
