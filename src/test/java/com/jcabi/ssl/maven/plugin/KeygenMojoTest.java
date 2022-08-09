/**
 * Copyright (c) 2012-2022, jcabi.com
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

import java.io.File;
import java.util.Properties;
import org.apache.maven.project.MavenProject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test case for {@link KeygenMojo} (more detailed test is in maven invoker).
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public final class KeygenMojoTest {

    /**
     * KeygenMojo can skip execution when flag is set.
     * @throws Exception If something is wrong
     */
    @Test
    public void skipsExecutionWhenRequired() throws Exception {
        final KeygenMojo mojo = new KeygenMojo();
        mojo.setSkip(true);
        mojo.execute();
    }

    /**
     * KeygenMojo populates cacerts even is keystore is active.
     * @throws Exception if test have failed
     */
    @Test
    public void populatesCacertsIdKeystoreIsActive() throws Exception {
        final Keystore keystore = new Keystore("changeit");
        keystore.activate(
            new File("target/populatesCacertsIdKeystoreIsActive/keystore.jks")
        );
        final Cacerts cacerts = new Cacerts(
            new File("target/populatesCacertsIdKeystoreIsActive/truststore.jks")
        );
        final MavenProject project = Mockito.mock(MavenProject.class);
        final Properties properties = new Properties();
        Mockito.when(project.getProperties()).thenReturn(properties);
        final KeygenMojo mojo = new KeygenMojo(project, keystore, cacerts);
        System.getProperties().setProperty(Cacerts.TRUST, "trust");
        System.getProperties().setProperty(Cacerts.TRUST_PWD, "pwd");
        mojo.execute();
        MatcherAssert.assertThat(
            properties.getProperty(Cacerts.TRUST_PWD),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            properties.getProperty(Cacerts.TRUST),
            Matchers.notNullValue()
        );
    }

}
