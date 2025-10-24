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
package com.childrengreens.web.context.i18n;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Default {@link MessageResolver} backed by a Spring {@link MessageSource}.
 */
public class MessageResolverImpl implements MessageResolver {

    private final MessageSource messageSource;

    public MessageResolverImpl(MessageSource messageSource) {
        Assert.notNull(messageSource, "messageSource must not be null");
        this.messageSource = messageSource;
    }

    @Override
    public String getMessage(String code, Object... args) {
        return getMessageInternal(code, LocaleContextHolder.getLocale(), args);
    }

    @Override
    public String getMessageForLocale(String code, Locale locale, Object... args) {
        return getMessageInternal(code, locale, args);
    }

    private String getMessageInternal(String code, @Nullable Locale locale, Object[] args) {
        Locale messageLocale = (locale != null ? locale : LocaleContextHolder.getLocale());
        return this.messageSource.getMessage(code, args, code, messageLocale);
    }
}
