/**
 * Copyright (c) 2012-2015, jcabi.com
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

import java.util.Locale;
import java.util.ResourceBundle;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration test for KeytoolBundle set of classes.
 * @author Georgy Vlasov (wlasowegor@gmail.com)
 * @version $Id$
 * @since 0.11
 */
public final class KeytoolBundleTest {

    /**
     * English word yes.
     */
    public static final String YES = "yes";

    /**
     * Translates yes to German.
     */
    @Test
    public void translatesYesToDe() {
        MatcherAssert.assertThat(
            this.translateYes("de"),
            Matchers.equalTo("ja")
        );
    }

    /**
     * Translates yes to Russian.
     */
    @Test
    public void translatesYesToRu() {
        MatcherAssert.assertThat(
            this.translateYes("ru"),
            Matchers.equalTo("да")
        );
    }

    /**
     * Translates yes to English.
     */
    @Test
    public void translatesYesToEn() {
        MatcherAssert.assertThat(
            this.translateYes("en"),
            Matchers.equalTo(YES)
        );
    }

    /**
     * Translates yes to a langage specified by a locale name.
     * @param locale Name of locale.
     * @return Word yes translated into a particular language.
     */
    private String translateYes(final String locale) {
        return ResourceBundle.getBundle(
            "com.jcabi.ssl.maven.plugin.KeytoolBundle",
            Locale.forLanguageTag(locale)
        ).getString(YES);
    }
}
