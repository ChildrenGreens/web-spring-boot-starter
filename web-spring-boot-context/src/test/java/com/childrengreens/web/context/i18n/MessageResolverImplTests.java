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
package com.childrengreens.web.context.i18n;

import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.StaticMessageSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class MessageResolverImplTests {

    private final StaticMessageSource messageSource = new StaticMessageSource();

    private final MessageResolverImpl resolver = new MessageResolverImpl(this.messageSource);

    @AfterEach
    void resetLocaleContext() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void constructorRejectsNullMessageSource() {
        assertThatIllegalArgumentException().isThrownBy(() -> new MessageResolverImpl(null))
                .withMessage("messageSource must not be null");
    }

    @Test
    void getMessageUsesLocaleContextHolderLocale() {
        LocaleContextHolder.setLocale(Locale.FRANCE);
        this.messageSource.addMessage("greeting", Locale.FRANCE, "Bonjour");
        String message = this.resolver.getMessage("greeting");
        assertThat(message).isEqualTo("Bonjour");
    }

    @Test
    void getMessageFallsBackToCodeWhenNotFound() {
        LocaleContextHolder.setLocale(Locale.US);
        assertThat(this.resolver.getMessage("missing.code")).isEqualTo("missing.code");
    }

    @Test
    void getMessageForLocaleResolvesRequestedLocale() {
        this.messageSource.addMessage("greeting", Locale.CHINA, "你好");
        String message = this.resolver.getMessageForLocale("greeting", Locale.CHINA);
        assertThat(message).isEqualTo("你好");
    }

    @Test
    void getMessageForLocaleUsesArguments() {
        this.messageSource.addMessage("welcome", Locale.US, "Hello {0}");
        String message = this.resolver.getMessageForLocale("welcome", Locale.US, "Alice");
        assertThat(message).isEqualTo("Hello Alice");
    }
}
