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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.io.FileUtils;

/**
 * Abstraction of {@code java.home/lib/security/cacerts} file.
 *
 * @since 0.5
 */
@Immutable
@ToString
@EqualsAndHashCode(of = "store")
final class Cacerts {

    /**
     * Constant {@code javax.net.ssl.trustStore}.
     */
    public static final String TRUST = "javax.net.ssl.trustStore";

    /**
     * Constant {@code javax.net.ssl.trustStorePassword}.
     */
    public static final String TRUST_PWD = "javax.net.ssl.trustStorePassword";

    /**
     * Standard password of {@code cacerts} file.
     */
    public static final String STD_PWD = "changeit";

    /**
     * New location of the trust store.
     */
    private final transient String store;

    /**
     * Ctor.
     * @param file New location
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    Cacerts(@NotNull final File file) throws IOException {
        this.store = file.getAbsolutePath();
        final File prev = new File(
            String.format(
                "%s/lib/security/cacerts",
                System.getProperty("java.home")
            )
        ).toPath().toRealPath().toFile();
        Cacerts.convert(prev, file);
        file.setWritable(true);
        Logger.info(
            this,
            "Existing cacerts '%s' imported to '%s' (%s)",
            prev,
            this.store,
            FileUtils.byteCountToDisplaySize(file.length())
        );
    }

    /**
     * Import existing keystore content into this trust store.
     * @throws IOException If fails
     */
    public void imprt() throws IOException {
        final File keystore = new File(System.getProperty(Keystore.KEY));
        new Keytool(new File(this.store), Cacerts.STD_PWD).imprt(
            keystore, System.getProperty(Keystore.KEY_PWD)
        );
        System.setProperty(Cacerts.TRUST, this.store);
        System.setProperty(Cacerts.TRUST_PWD, Cacerts.STD_PWD);
        Logger.info(
            this,
            "keyStore '%s' imported into trustStore '%s'",
            keystore,
            this.store
        );
    }

    /**
     * Populate given properties with this truststore's path and password.
     *
     * @param props The properties
     */
    @Loggable(Loggable.DEBUG)
    public void populate(final Properties props) {
        final String[] names = {Cacerts.TRUST, Cacerts.TRUST_PWD};
        for (final String name : names) {
            final String value = System.getProperty(name);
            if (value == null) {
                continue;
            }
            props.put(name, value);
            Logger.info(
                this,
                "Maven property ${%s} set to '%s'",
                name,
                value
            );
        }
    }

    /**
     * Convert cacerts from any format to JKS.
     * @param src Source cacerts file
     * @param dest Destination JKS file
     * @throws IOException If fails
     */
    private static void convert(final File src, final File dest)
        throws IOException {
        dest.getParentFile().mkdirs();
        final List<String> cmds = new ArrayList<>(15);
        cmds.add(
            String.format(
                "%s/bin/keytool",
                System.getProperty("java.home")
            )
        );
        cmds.add("-importkeystore");
        cmds.add("-srckeystore");
        cmds.add(src.getAbsolutePath());
        cmds.add("-srcstorepass");
        cmds.add(Cacerts.STD_PWD);
        cmds.add("-destkeystore");
        cmds.add(dest.getAbsolutePath());
        cmds.add("-deststorepass");
        cmds.add(Cacerts.STD_PWD);
        cmds.add("-deststoretype");
        cmds.add("jks");
        cmds.add("-noprompt");
        final ProcessBuilder builder = new ProcessBuilder(cmds);
        builder.environment().put(
            "JAVA_TOOL_OPTIONS",
            "-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8"
        );
        new VerboseProcess(builder, Level.FINE, Level.FINE).stdout();
    }
}
