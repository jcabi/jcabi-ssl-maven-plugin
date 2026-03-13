/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.ssl.maven.plugin;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
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
        );
        FileUtils.copyFile(prev.toPath().toRealPath().toFile(), file);
        file.setWritable(true);
        Logger.info(
            this,
            "Existing cacerts '%s' copied to '%s' (%s)",
            prev.toPath().toRealPath(),
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
}
