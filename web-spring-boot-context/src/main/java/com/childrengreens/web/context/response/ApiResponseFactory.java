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

package com.childrengreens.web.context.response;

import java.util.Objects;
import java.util.function.Supplier;

import com.childrengreens.web.context.exception.DefaultErrorCode;
import com.childrengreens.web.context.exception.ErrorCode;

/**
 * Factory that centralises the creation of {@link ApiResponse} instances.
 */
public final class ApiResponseFactory {

    private final Supplier<String> successCodeSupplier;

    private final Supplier<String> successMessageSupplier;

    private final Supplier<String> defaultErrorCodeSupplier;

    private final Supplier<String> defaultErrorMessageSupplier;

    public ApiResponseFactory() {
        this(DefaultErrorCode.SUCCESS::getCode, DefaultErrorCode.SUCCESS::getMessage,
                DefaultErrorCode.INTERNAL_ERROR::getCode, DefaultErrorCode.INTERNAL_ERROR::getMessage);
    }

    public ApiResponseFactory(Supplier<String> successCodeSupplier, Supplier<String> successMessageSupplier,
            Supplier<String> defaultErrorCodeSupplier, Supplier<String> defaultErrorMessageSupplier) {
        this.successCodeSupplier = Objects.requireNonNull(successCodeSupplier, "successCodeSupplier");
        this.successMessageSupplier = Objects.requireNonNull(successMessageSupplier, "successMessageSupplier");
        this.defaultErrorCodeSupplier = Objects.requireNonNull(defaultErrorCodeSupplier, "defaultErrorCodeSupplier");
        this.defaultErrorMessageSupplier = Objects.requireNonNull(defaultErrorMessageSupplier,
                "defaultErrorMessageSupplier");
    }

    public <T> ApiResponse<T> success(T data) {
        return ApiResponse.of(this.successCodeSupplier.get(), this.successMessageSupplier.get(), data);
    }

    public ApiResponse<Void> success() {
        return ApiResponse.of(this.successCodeSupplier.get(), this.successMessageSupplier.get(), null);
    }

    public ApiResponse<Void> failure(String message) {
        return ApiResponse.of(this.defaultErrorCodeSupplier.get(), message, null);
    }

    public ApiResponse<Void> failure(ErrorCode errorCode) {
        return ApiResponse.failure(errorCode);
    }

    public ApiResponse<Void> failure(ErrorCode errorCode, String message) {
        return ApiResponse.failure(errorCode, message);
    }
}
