/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.ssl.maven.plugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Keytool}.
 *
 * @since 0.5
 */
final class KeytoolTest {

    /**
     * Keytool can generate a keystore.
     *
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

    /**
     * Keytool can log the real size of the keystore file.
     *
     * @param temp Temporary directory
     * @throws Exception If something is wrong
     */
    @Test
    void logsFileSizeOfKeystore(@TempDir final Path temp) throws Exception {
        final File file = temp.resolve("keystore.jks").toFile();
        final ByteArrayOutputStream log = new ByteArrayOutputStream();
        try (
            KeytoolTest.Restore restore = new KeytoolTest.Restore();
            PrintStream stream = new PrintStream(
                log, true, StandardCharsets.UTF_8
            )
        ) {
            restore.redirect(stream);
            new Keytool(file, "some-password").genkey();
            MatcherAssert.assertThat(
                "keystore size is missing in the log",
                log.toString(StandardCharsets.UTF_8),
                Matchers.containsString(
                    FileUtils.byteCountToDisplaySize(file.length())
                )
            );
        }
    }

    /**
     * Standard output that is put back when closed.
     *
     * @since 0.5
     */
    private static final class Restore implements AutoCloseable {

        /**
         * Standard output to put back.
         */
        private final PrintStream origin;

        /**
         * Ctor.
         */
        Restore() {
            this.origin = System.out;
        }

        @Override
        public void close() {
            System.setOut(this.origin);
        }

        /**
         * Send the standard output to the stream.
         *
         * @param stream The stream
         */
        void redirect(final PrintStream stream) {
            System.setOut(stream);
        }
    }
}
