/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.ssl.maven.plugin;

import java.util.Locale;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Unit tests for {@link Yes}.
 *
 * @since 0.12
 */
public final class YesTest {
    /**
     * Yes can be translated to different languages.
     */
    @Test
    public void translates() {
        MatcherAssert.assertThat(
            new Yes().translate(Locale.FRENCH),
            Matchers.equalTo("oui")
        );
    }

}
