/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.ssl.maven.plugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Translates word yes to different languages.
 *
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
     * Ctor.
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
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
