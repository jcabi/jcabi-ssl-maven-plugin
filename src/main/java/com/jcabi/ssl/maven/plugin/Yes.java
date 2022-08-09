/**
 * Copyright (c) 2012-2022, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.ssl.maven.plugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Translates word yes to different languages.
 * @author Georgy Vlasov (wlasowegor@gmail.com)
 * @version $Id$
 * @since 0.12
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
public final class Yes {
    /**
     * Map from 2-letter language codes to translations of word yes into that
     * language.
     */
    private final transient Map<String, String> translations;

    /**
     * Public ctor.
     */
    public Yes() {
        this.translations = new HashMap<String, String>();
        this.translations.put("en", "yes");
        this.translations.put("de", "ja");
        this.translations.put("fr", "oui");
        this.translations.put("ru", "да");
        this.translations.put("es", "sí");
        this.translations.put("ua", "так");
        this.translations.put("jp", "はい");
    }

    /**
     * Translates word yes to a language.
     * @param locale Locate specifying the language.
     * @return Word yes translated to a language.
     */
    public String translate(final Locale locale) {
        final String language = locale.getLanguage();
        final String translation = this.translations.get(language);
        if (translation == null) {
            throw new IllegalArgumentException(
                String.format(
                    new StringBuilder()
                        .append("Language %s is not supported, you can create ")
                        .append("an issue on Github and we'll fix it")
                        .toString(),
                    language
                )
            );
        }
        return translation;
    }
}
