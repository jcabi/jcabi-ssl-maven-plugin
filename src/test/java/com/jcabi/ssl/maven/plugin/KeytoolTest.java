/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.ssl.maven.plugin;

import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Keytool}.
 * @since 0.5
 */
final class KeytoolTest {

    /**
     * Keytool can generate a keystore.
     * @param temp Temporary directory
     * @throws Exception If something is wrong
     */
    @Test
    void generatesAndActivatesKeystore(@TempDir final Path temp)
        throws Exception {
        final Keytool keytool = new Keytool(
            temp.resolve("keystore.jks").toFile(), "some-password"
        );
        keytool.genkey();
        MatcherAssert.assertThat(
            "alias cannot be found in the keystore",
            keytool.list(),
            Matchers.containsString("Alias name:")
        );
    }
}
