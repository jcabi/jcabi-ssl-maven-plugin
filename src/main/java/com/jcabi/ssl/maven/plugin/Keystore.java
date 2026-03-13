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

/**
 * Keystore abstraction.
 *
 * @since 0.5
 */
@Immutable
@EqualsAndHashCode(of = "password")
final class Keystore {

    /**
     * Constant {@code javax.net.ssl.keyStore}.
     */
    public static final String KEY = "javax.net.ssl.keyStore";

    /**
     * Constant {@code javax.net.ssl.keyStorePassword}.
     */
    public static final String KEY_PWD = "javax.net.ssl.keyStorePassword";

    /**
     * Unique password of it.
     */
    private final transient String password;

    /**
     * Ctor.
     * @param pwd The password
     */
    Keystore(@NotNull final String pwd) {
        this.password = pwd;
    }

    @Override
    public String toString() {
        final String[] names = {Keystore.KEY, Keystore.KEY_PWD};
        final StringBuilder text = new StringBuilder();
        text.append('[');
        for (final String name : names) {
            if (text.length() > 1) {
                text.append(", ");
            }
            text.append(name).append('=');
            if (name == null) {
                text.append("NULL");
            } else {
                text.append(System.getProperty(name));
            }
        }
        text.append(']');
        return text.toString();
    }

    /**
     * Is it active now in the JVM?
     * @return TRUE if JVM is using our keystore
     */
    @Loggable(Loggable.DEBUG)
    public boolean isActive() {
        final String pwd = System.getProperty(Keystore.KEY_PWD);
        return pwd != null && pwd.equals(this.password);
    }

    /**
     * Activate it, in the given file.
     * @param file The file to use
     * @throws IOException If fails
     */
    @Loggable(Loggable.DEBUG)
    public void activate(final File file) throws IOException {
        file.getParentFile().mkdirs();
        file.delete();
        new Keytool(file, this.password).genkey();
        System.setProperty(Keystore.KEY, file.getAbsolutePath());
        System.setProperty(Keystore.KEY_PWD, this.password);
        new Keytool(file, this.password).list();
    }

    /**
     * Populate given properties with this keystore's path and password.
     * @param props The properties
     */
    @Loggable(Loggable.DEBUG)
    public void populate(final Properties props) {
        final String[] names = {Keystore.KEY, Keystore.KEY_PWD};
        for (final String name : names) {
            if (System.getProperty(name) == null) {
                continue;
            }
            props.put(name, System.getProperty(name));
            Logger.info(
                this,
                "Maven property ${%s} set to '%s'",
                name,
                System.getProperty(name)
            );
        }
    }

}
