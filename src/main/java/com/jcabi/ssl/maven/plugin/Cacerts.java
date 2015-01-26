/**
 * Copyright (c) 2012-2013, JCabi.com
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
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 */
@Immutable
@ToString
@EqualsAndHashCode(of = "store")
class Cacerts {

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
     * Public ctor.
     * @param file New location
     * @throws IOException If fails
     */
    public Cacerts(@NotNull final File file) throws IOException {
        this.store = file.getAbsolutePath();
        final File prev = new File(
            String.format(
                "%s/lib/security/cacerts",
                System.getProperty("java.home")
            )
        );
        FileUtils.copyFile(prev, file);
        Logger.info(
            this,
            "Existing cacerts '%s' copied to '%s' (%s)",
            prev,
            this.store,
            FileUtils.byteCountToDisplaySize(this.store.length())
        );
    }

    /**
     * Import existing keystore content into this trust store.
     * @throws IOException If fails
     */
    public void imprt() throws IOException {
        final File keystore = new File(System.getProperty(Keystore.KEY));
        final String pwd = System.getProperty(Keystore.KEY_PWD);
        new Keytool(new File(this.store), Cacerts.STD_PWD).imprt(keystore, pwd);
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
        final String[] names = new String[] {Cacerts.TRUST, Cacerts.TRUST_PWD};
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
