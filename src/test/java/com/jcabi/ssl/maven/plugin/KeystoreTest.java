/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.ssl.maven.plugin;

import java.io.File;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Keystore}.
 * @since 0.5
 */
final class KeystoreTest {

    /**
     * Keystore can generate a file.
     * @param temp Temporary directory
     * @throws Exception If something is wrong
     */
    @Test
    void generatesAndActivatesKeystore(@TempDir final Path temp)
        throws Exception {
        final Keystore keystore = new Keystore("test-test");
        keystore.activate(
            new File(temp.resolve("tmp").toFile(), "/a/b/ckeystore.jks")
        );
        MatcherAssert.assertThat(
            "keystore cannot be activated",
            keystore.isActive(),
            Matchers.is(true)
        );
    }
}
