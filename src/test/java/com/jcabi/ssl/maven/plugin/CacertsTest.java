/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.ssl.maven.plugin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Cacerts}.
 * @since 0.5
 */
final class CacertsTest {

    /**
     * Cacerts can generate a keystore.
     * @param temp Temporary directory
     * @throws Exception If something is wrong
     */
    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void importsCertificatesFromKeystore(@TempDir final Path temp)
        throws Exception {
        final File truststore = temp.resolve("cacerts.jks").toFile();
        new Keystore("some-password").activate(
            temp.resolve("keystore.jks").toFile()
        );
        final Cacerts cacerts = new Cacerts(truststore);
        cacerts.imprt();
        MatcherAssert.assertThat(
            "localhost cannot be found in the trust store",
            new Keytool(truststore, "changeit").list(),
            Matchers.containsString("localhost")
        );
        final Properties props = new Properties();
        cacerts.populate(props);
        MatcherAssert.assertThat(
            "trust store location cannot be set",
            truststore.getAbsolutePath(),
            Matchers.equalTo(props.getProperty(Cacerts.TRUST))
        );
        MatcherAssert.assertThat(
            "trust store password cannot be set",
            Cacerts.STD_PWD,
            Matchers.equalTo(props.getProperty(Cacerts.TRUST_PWD))
        );
    }

    /**
     * Cacerts copies content from symlink correctly when cacerts is a symlink.
     * @param temp Temporary directory
     * @throws Exception If something is wrong
     */
    @Test
    void copiesFromSymlinkCorrectly(@TempDir final Path temp) throws Exception {
        final File original = temp.resolve(UUID.randomUUID().toString())
            .toFile();
        FileUtils.writeStringToFile(
            original,
            UUID.randomUUID().toString().repeat(100),
            StandardCharsets.UTF_8
        );
        final Path link = temp.resolve(UUID.randomUUID().toString());
        Files.createSymbolicLink(link, original.toPath());
        Assumptions.assumeTrue(
            Files.isSymbolicLink(link),
            "Symlinks not supported on this system"
        );
        final File destination = temp.resolve(UUID.randomUUID().toString())
            .toFile();
        FileUtils.copyFile(link.toRealPath().toFile(), destination);
        MatcherAssert.assertThat(
            "copied file size cannot match the original one",
            destination.length(),
            Matchers.equalTo(original.length())
        );
    }
}
