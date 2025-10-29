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

/**
 * Default set of error codes used by the web starter.
 */
public enum DefaultErrorCode implements ErrorCode {

    SUCCESS("0", "Success"),

    VALIDATION_ERROR("1000", "Validation failed"),

    BUSINESS_ERROR("1001", "Business rule violated"),

    UNAUTHORIZED("1002", "Unauthorized"),

    RESOURCE_NOT_FOUND("1004", "Resource not found"),

    INTERNAL_ERROR("1999", "Internal server error");

    private final String code;

    private final String message;

    DefaultErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
