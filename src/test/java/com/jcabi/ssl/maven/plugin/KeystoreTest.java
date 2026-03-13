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
 * Test case for {@link Keystore}.
 *
 * @since 0.5
 */
public final class KeystoreTest {

    /**
     * Temporary folder.
     * @checkstyle VisibilityModifier (3 lines)
     */
    @Rule
    public transient TemporaryFolder temp = new TemporaryFolder();

    /**
     * Keystore can generate a file.
     * @throws Exception If something is wrong
     */
    @Test
    public void generatesAndActivatesKeystore() throws Exception {
        final Keystore keystore = new Keystore("test-test");
        keystore.activate(
            new File(this.temp.newFolder("tmp"), "/a/b/ckeystore.jks")
        );
        MatcherAssert.assertThat(keystore.isActive(), Matchers.is(true));
    }

}
