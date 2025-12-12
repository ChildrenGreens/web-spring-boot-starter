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

import com.childrengreens.web.context.response.ApiResponse;
import com.childrengreens.web.context.response.ApiResponseFactory;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTests {

    private final ApiResponseFactory responseFactory = new ApiResponseFactory();

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(this.responseFactory);

    @Test
    // Generic exceptions should be sanitized to avoid leaking sensitive details
    void handleGenericExceptionReturnsSanitisedMessage() {
        Exception sensitive = new IllegalStateException("database details should not leak");
        WebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<ApiResponse<Void>> response = this.handler.handleGenericException(sensitive, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(DefaultErrorCode.INTERNAL_ERROR.getMessage());
    }
}
