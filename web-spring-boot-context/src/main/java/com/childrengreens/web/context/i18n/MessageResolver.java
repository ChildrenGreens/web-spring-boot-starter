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

/**
 * Resolves internationalised messages optionally using the caller provided locale.
 */
public interface MessageResolver {

    /**
     * Resolve a message for the given code using the current request locale.
     *
     * @param code message key
     * @param args optional arguments used for formatting
     * @return resolved message or the code when no message was found
     */
    String getMessage(String code, Object... args);

    /**
     * Resolve a message for the given locale.
     *
     * @param code message key
     * @param locale preferred locale
     * @param args optional arguments used for formatting
     * @return resolved message or the code when no message was found
     */
    String getMessageForLocale(String code, Locale locale, Object... args);
}
