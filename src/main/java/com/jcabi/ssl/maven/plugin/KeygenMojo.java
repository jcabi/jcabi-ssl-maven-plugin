/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.ssl.maven.plugin;

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Generate SSL keystore and configure in JVM.
 *
 * @since 0.5
 * @goal keygen
 * @phase initialize
 */
public final class KeygenMojo extends AbstractMojo {

    /**
     * Maven project.
     * @parameter name="project" default-value="${project}"
     * @readonly
     * @required
     */
    private transient MavenProject project;

    /**
     * Shall we skip execution?
     * @parameter name="skip"
     */
    private transient boolean skip;

    /**
     * Name of keystore.jks file.
     * @parameter name="keystore"
     * default-value="${project.build.directory}/keystore.jks"
     */
    private transient File keystore;

    /**
     * Name of cacerts.jks file.
     * @parameter name="cacerts"
     * default-value="${project.build.directory}/cacerts.jks"
     */
    private transient File cacerts;

    /**
     * Keystore instance.
     */
    private transient Keystore store;

    /**
     * Cacerts instance.
     */
    private transient Cacerts truststore;

    /**
     * Creates KeygenMojo.
     */
    public KeygenMojo() {
        this(
            null, new Keystore(DigestUtils.md5Hex(KeygenMojo.class.getName())),
            null
        );
    }

    /**
     * Creates KeygenMojo using custom KeystoreFactory.
     * @param prj Maven project
     * @param str Keystore instance
     * @param crt Cacerts instance
     */
    public KeygenMojo(final MavenProject prj, final Keystore str,
        final Cacerts crt) {
        super();
        this.project = prj;
        this.store = str;
        this.truststore = crt;
    }

    /**
     * Set skip option.
     * @param skp Shall we skip execution?
     */
    public void setSkip(final boolean skp) {
        this.skip = skp;
    }

    @Override
    public void execute() throws MojoFailureException {
        StaticLoggerBinder.getSingleton().setMavenLog(this.getLog());
        if (this.skip) {
            Logger.info(this, "execution skipped because of 'skip' option");
            return;
        }
        try {
            if (this.truststore == null) {
                this.truststore = new Cacerts(this.cacerts);
            }
            if (!this.store.isActive()) {
                this.store.activate(this.keystore);
                this.truststore.imprt();
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        this.store.populate(this.project.getProperties());
        this.truststore.populate(this.project.getProperties());
        Logger.info(this, "Keystore is active: %s", this.store);
    }

}
