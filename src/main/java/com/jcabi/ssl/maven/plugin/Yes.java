/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.ssl.maven.plugin;

import java.util.Locale;
import java.util.Map;

/**
 * Translates word yes to different languages.
 * @since 0.12
 */
public final class Yes {

    /**
     * Map from 2-letter language codes to translations of word yes into that
     * language.
     */
    private static final Map<String, String> TRANSLATIONS = Map.of(
        "en", "yes",
        "de", "ja",
        "fr", "oui",
        "ru", "да",
        "es", "sí",
        "ua", "так",
        "jp", "はい"
    );

    /**
     * Message about an unsupported language.
     */
    private static final String UNSUPPORTED = String.join(
        " ",
        "Language %s is not supported, you can create",
        "an issue on Github and we'll fix it"
    );

    /**
     * The locale, whose language the word is translated to.
     */
    private final transient Locale locale;

    /**
     * Ctor.
     * @param loc The locale
     */
    public Yes(final Locale loc) {
        this.locale = loc;
    }

    /**
     * Translates word yes to the language of the locale.
     * @return Word yes translated to a language
     */
    public String translate() {
        final String language = this.locale.getLanguage();
        final String translation = Yes.TRANSLATIONS.get(language);
        if (translation == null) {
            throw new IllegalArgumentException(
                String.format(Yes.UNSUPPORTED, language)
            );
        }
        return translation;
    }
}
