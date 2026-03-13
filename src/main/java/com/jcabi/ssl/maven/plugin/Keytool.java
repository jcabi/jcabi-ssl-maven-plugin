/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.ssl.maven.plugin;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.log.Logger;
import com.jcabi.log.VerboseProcess;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.io.FileUtils;

/**
 * Keytool abstraction.
 *
 * @since 0.5
 */
@Immutable
@ToString
@EqualsAndHashCode(of = { "keystore", "password" })
final class Keytool {
    /**
     * Localhost, input to the keytool.
     */
    private static final String LOCALHOST = "localhost";

    /**
     * Platform-dependent line separator.
     */
    private static final String NEWLINE = System.getProperty("line.separator");

    /**
     * Keystore location.
     */
    private final transient String keystore;

    /**
     * Keystore password.
     */
    private final transient String password;

    /**
     * Ctor.
     * @param store The location of keystore
     * @param pwd The password
     */
    Keytool(final File store, final String pwd) {
        this.keystore = store.getAbsolutePath();
        this.password = pwd;
    }

    /**
     * List content of the keystore.
     * @return The content of it
     * @throws IOException If fails
     */
    @Loggable(Loggable.DEBUG)
    public String list() throws IOException {
        final List<String> cmds = new ArrayList<>(10);
        cmds.add(Keytool.keytool());
        cmds.add("-list");
        cmds.add("-v");
        cmds.add("-keystore");
        cmds.add(this.keystore);
        cmds.add("-storepass");
        cmds.add(this.password);
        return new VerboseProcess(
            Keytool.utf(new ProcessBuilder(cmds)), Level.FINE, Level.FINE
        ).stdout();
    }

    /**
     * Generate key.
     * @throws IOException If fails
     */
    @Loggable(Loggable.DEBUG)
    public void genkey() throws IOException {
        final Process proc = Keytool.utf(
            this.proc(
                "-genkeypair",
                "-alias",
                Keytool.LOCALHOST,
                "-keyalg",
                "RSA",
                "-keysize",
                "2048",
                "-keypass",
                this.password
            )
        ).start();
        try (PrintWriter writer = new PrintWriter(
            new OutputStreamWriter(proc.getOutputStream(), StandardCharsets.UTF_8)
        )) {
            writer.print(Keytool.appendNewLine(Keytool.LOCALHOST));
            writer.print(Keytool.appendNewLine("ACME Co."));
            writer.print(Keytool.appendNewLine("software developers"));
            writer.print(Keytool.appendNewLine("San Francisco"));
            writer.print(Keytool.appendNewLine("California"));
            writer.print(Keytool.appendNewLine("US"));
            writer.print(Keytool.appendNewLine(Keytool.localeDependentYes()));
        }
        new VerboseProcess(proc, Level.FINE, Level.FINE).stdout();
        Logger.info(
            this,
            "Keystore created in '%s' (%s)",
            this.keystore,
            FileUtils.byteCountToDisplaySize(this.keystore.length())
        );
    }

    /**
     * Import certificate into this store.
     * @param file The file to import
     * @param pwd The password there
     * @throws IOException If fails
     */
    @Loggable(Loggable.DEBUG)
    public void imprt(final File file, final String pwd) throws IOException {
        final List<String> cmds = new ArrayList<>(20);
        cmds.add(Keytool.keytool());
        cmds.add("-importkeystore");
        cmds.add("-srckeystore");
        cmds.add(file.getAbsolutePath());
        cmds.add("-srcstorepass");
        cmds.add(pwd);
        cmds.add("-srcalias");
        cmds.add(Keytool.LOCALHOST);
        cmds.add("-srckeypass");
        cmds.add(pwd);
        cmds.add("-srcstoretype");
        cmds.add("jks");
        cmds.add("-destkeystore");
        cmds.add(this.keystore);
        cmds.add("-deststorepass");
        cmds.add(this.password);
        cmds.add("-destkeypass");
        cmds.add(this.password);
        cmds.add("-deststoretype");
        cmds.add("jks");
        cmds.add("-noprompt");
        new VerboseProcess(
            Keytool.utf(new ProcessBuilder(cmds)), Level.FINE, Level.FINE
        ).stdout();
    }

    /**
     * Creates a string, which consists of string with an appended
     * platform-dependent line separator.
     * @param text Text, to which the line separator needs to be appended
     * @return Contents of text with appended line separator
     */
    private static String appendNewLine(final String text) {
        return String.format("%s%s", text, Keytool.NEWLINE);
    }

    /**
     * Creates a text, which represents "yes" in the language,
     * specified by the current locale.
     * @return The word "Yes" translated to the current language
     */
    private static String localeDependentYes() {
        return new Yes().translate(Locale.getDefault());
    }

    /**
     * Returns the path to the keytool executable.
     * @return Path to keytool
     */
    private static String keytool() {
        return String.format(
            "%s/bin/keytool",
            System.getProperty("java.home")
        );
    }

    /**
     * Create process builder.
     * @param args Arguments
     * @return Process just created and started
     */
    private ProcessBuilder proc(final String... args) {
        final List<String> cmds = new ArrayList<>(args.length + 1);
        cmds.add(Keytool.keytool());
        cmds.addAll(java.util.Arrays.asList(args));
        cmds.add("-storetype");
        cmds.add("jks");
        cmds.add("-noprompt");
        cmds.add("-storepass");
        cmds.add(this.password);
        cmds.add("-keystore");
        cmds.add(this.keystore);
        return new ProcessBuilder(cmds);
    }

    /**
     * Configure process builder to use UTF-8 encoding.
     * @param builder The process builder
     * @return The same builder with UTF-8 environment
     */
    private static ProcessBuilder utf(final ProcessBuilder builder) {
        builder.environment().put(
            "JAVA_TOOL_OPTIONS",
            "-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8"
        );
        return builder;
    }
}
