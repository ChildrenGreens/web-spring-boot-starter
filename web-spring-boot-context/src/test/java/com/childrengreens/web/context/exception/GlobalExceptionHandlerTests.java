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
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTests {

    private final ApiResponseFactory responseFactory = new ApiResponseFactory();

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(this.responseFactory);

    @Test
    void handleBusinessExceptionUsesErrorCode() {
        BusinessException businessException = new BusinessException(DefaultErrorCode.UNAUTHORIZED, "denied");

        ResponseEntity<ApiResponse<Void>> response = this.handler.handleBusinessException(businessException);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(DefaultErrorCode.UNAUTHORIZED.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo("denied");
    }

    @Test
    void handleBindingErrorsAggregatesFieldAndGlobalMessages() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "name", "must not be blank"));
        bindingResult.reject("global", "global issue");
        BindException bindException = new BindException(bindingResult);

        ResponseEntity<ApiResponse<Void>> response = this.handler.handleBindingErrors(bindException);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("name: must not be blank")
                .contains("global issue");
    }

    @Test
    void handleBadRequestCoversValidationExceptions() {
        ConstraintViolationException violation = new ConstraintViolationException("invalid input", null);

        ResponseEntity<ApiResponse<Void>> response = this.handler.handleBadRequest(violation);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("invalid input");
    }

    @Test
    void handleMessageNotReadableUsesMostSpecificCauseMessage() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("body error",
                new RuntimeException("root cause"), null);

        ResponseEntity<ApiResponse<Void>> response = this.handler.handleMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("root cause");
    }

    @Test
    void handleUnsupportedMediaUsesFactory() {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("text/plain");

        ResponseEntity<ApiResponse<Void>> response = this.handler.handleUnsupportedMedia(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(ex.getMessage());
    }

    @Test
    void handleMethodNotSupportedUsesFactory() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("PATCH",
                java.util.List.of("GET"));

        ResponseEntity<ApiResponse<Void>> response = this.handler.handleMethodNotSupported(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(ex.getMessage());
    }

    @Test
    void handleNotFoundReturnsRequestedUrl() {
        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/missing", new HttpHeaders());

        ResponseEntity<ApiResponse<Void>> response = this.handler.handleNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("/missing");
    }

    @Test
    void handleUnauthorizedFallsBackToDefaultMessage() {
        UnauthorizedException ex = new UnauthorizedException(null);

        ResponseEntity<ApiResponse<Void>> response = this.handler.handleUnauthorized(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(DefaultErrorCode.UNAUTHORIZED.getMessage());
    }

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
