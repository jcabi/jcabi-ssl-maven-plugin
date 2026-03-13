/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.ssl.maven.plugin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
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

    /**
     * Cacerts copies content from symlink correctly when cacerts is a symlink.
     * @throws Exception If something is wrong
     */
    @Test
    public void copiesFromSymlinkCorrectly() throws Exception {
        final File original = this.temp.newFile(
            UUID.randomUUID().toString()
        );
        FileUtils.writeStringToFile(
            original,
            UUID.randomUUID().toString().repeat(100),
            StandardCharsets.UTF_8
        );
        final File link = new File(
            this.temp.getRoot(),
            UUID.randomUUID().toString()
        );
        Files.createSymbolicLink(link.toPath(), original.toPath());
        Assume.assumeTrue(
            "Symlinks not supported on this system",
            Files.isSymbolicLink(link.toPath())
        );
        final File destination = this.temp.newFile(
            UUID.randomUUID().toString()
        );
        destination.delete();
        FileUtils.copyFile(
            link.toPath().toRealPath().toFile(),
            destination
        );
        MatcherAssert.assertThat(
            "Copied file size must match original file size when copying from symlink",
            destination.length(),
            Matchers.equalTo(original.length())
        );
    }
}
