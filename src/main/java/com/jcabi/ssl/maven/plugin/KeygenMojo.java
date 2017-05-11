/**
 * Copyright (c) 2012-2017, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
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

    /**
     * {@inheritDoc}
     */
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
