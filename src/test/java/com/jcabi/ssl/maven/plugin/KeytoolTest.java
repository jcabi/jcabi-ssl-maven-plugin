/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.ssl.maven.plugin;

import java.io.File;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test case for {@link Keytool}.
 *
 * @since 0.5
 */
public final class KeytoolTest {

    /**
     * Temporary folder.
     * @checkstyle VisibilityModifier (3 lines)
     */
    @Rule
    public transient TemporaryFolder temp = new TemporaryFolder();

    /**
     * Keytool can generate a keystore.
     * @throws Exception If something is wrong
     */
    @Test
    public void generatesAndActivatesKeystore() throws Exception {
        final File file = this.temp.newFile("keystore.jks");
        file.delete();
        final Keytool keytool = new Keytool(file, "some-password");
        keytool.genkey();
        MatcherAssert.assertThat(
            keytool.list(),
            Matchers.containsString("Alias name:")
        );
    }

}
