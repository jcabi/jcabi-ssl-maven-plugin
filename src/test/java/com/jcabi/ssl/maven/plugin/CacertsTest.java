/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.ssl.maven.plugin;

import java.io.File;
import java.util.Properties;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test case for {@link Cacerts}.
 *
 * @since 0.5
 */
public final class CacertsTest {

    /**
     * Temporary folder.
     * @checkstyle VisibilityModifier (3 lines)
     */
    @Rule
    public transient TemporaryFolder temp = new TemporaryFolder();

    /**
     * Cacerts can generate a keystore.
     * @throws Exception If something is wrong
     */
    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    public void importsCertificatesFromKeystore() throws Exception {
        final File keystore = this.temp.newFile("keystore.jks");
        keystore.delete();
        final File truststore = this.temp.newFile("cacerts.jks");
        truststore.delete();
        new Keystore("some-password").activate(keystore);
        final Cacerts cacerts = new Cacerts(truststore);
        cacerts.imprt();
        MatcherAssert.assertThat(
            new Keytool(truststore, "changeit").list(),
            Matchers.containsString("localhost")
        );
        final Properties props = new Properties();
        cacerts.populate(props);
        MatcherAssert.assertThat(
            truststore.getAbsolutePath(),
            Matchers.equalTo(props.getProperty(Cacerts.TRUST))
        );
        MatcherAssert.assertThat(
            Cacerts.STD_PWD,
            Matchers.equalTo(props.getProperty(Cacerts.TRUST_PWD))
        );
    }
}
