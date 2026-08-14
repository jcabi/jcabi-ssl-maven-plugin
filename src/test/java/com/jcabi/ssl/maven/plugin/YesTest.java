/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.ssl.maven.plugin;

import java.util.Locale;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Yes}.
 * @since 0.12
 */
final class YesTest {

    /**
     * Yes can be translated to different languages.
     */
    @Test
    void translates() {
        MatcherAssert.assertThat(
            "French cannot be translated",
            new Yes(Locale.FRENCH).translate(),
            Matchers.equalTo("oui")
        );
    }
}
