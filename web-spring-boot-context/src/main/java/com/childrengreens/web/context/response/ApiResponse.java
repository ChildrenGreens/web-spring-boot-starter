/*
 *  * Copyright @inceptionYear@-@currentYear@ the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */
package com.childrengreens.web.context.response;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import com.childrengreens.web.context.trace.TraceIdHolder;

import com.childrengreens.web.context.exception.DefaultErrorCode;
import com.childrengreens.web.context.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Common response wrapper with a predictable structure.
 *
 * @param <T> payload type
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ApiResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String code;

    private final String message;

    private final T data;

    private final String traceId;

    private ApiResponse(String code, String message, T data, String traceId) {
        this.code = Objects.requireNonNull(code, "code");
        this.message = Objects.requireNonNull(message, "message");
        this.data = data;
        this.traceId = traceId;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public T getData() {
        return this.data;
    }

    public String getTraceId() {
        return this.traceId;
    }

    public static <T> ApiResponse<T> success(T data) {
        return of(DefaultErrorCode.SUCCESS, data);
    }

    public static ApiResponse<Void> success() {
        return of(DefaultErrorCode.SUCCESS, null);
    }

    public static ApiResponse<Void> failure(String message) {
        return of(DefaultErrorCode.INTERNAL_ERROR.getCode(), message, null);
    }

    public static ApiResponse<Void> failure(ErrorCode errorCode) {
        return of(errorCode, null);
    }

    public static ApiResponse<Void> failure(ErrorCode errorCode, String message) {
        return of(errorCode.getCode(), message, null);
    }

    public static <T> ApiResponse<T> of(ErrorCode errorCode, T data) {
        return of(errorCode.getCode(), errorCode.getMessage(), data);
    }

    public static <T> ApiResponse<T> of(String code, String message, T data) {
        return new ApiResponse<>(code, message, data, resolveTraceId());
    }

    private static String resolveTraceId() {
        return TraceIdHolder.get();
    }
}
