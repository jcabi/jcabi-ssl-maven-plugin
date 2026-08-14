/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.ssl.maven.plugin;

import java.io.File;
import java.util.Properties;
import org.apache.maven.project.MavenProject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link KeygenMojo} (more detailed test is in maven invoker).
 * @since 0.5
 */
final class KeygenMojoTest {

    /**
     * KeygenMojo can skip execution when flag is set.
     * @throws Exception If something is wrong
     */
    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void skipsExecutionWhenRequired() throws Exception {
        final KeygenMojo mojo = new KeygenMojo();
        mojo.setSkip(true);
        mojo.execute();
    }

    /**
     * KeygenMojo populates cacerts even is keystore is active.
     * @throws Exception if test have failed
     */
    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void populatesCacertsIdKeystoreIsActive() throws Exception {
        final Keystore keystore = new Keystore("changeit");
        keystore.activate(
            new File("target/populatesCacertsIdKeystoreIsActive/keystore.jks")
        );
        final MavenProject project = Mockito.mock(MavenProject.class);
        final Properties properties = new Properties();
        Mockito.when(project.getProperties()).thenReturn(properties);
        final KeygenMojo mojo = new KeygenMojo(
            project, keystore,
            new Cacerts(
                new File("target/populatesCacertsIdKeystoreIsActive/trust.jks")
            )
        );
        System.getProperties().setProperty(Cacerts.TRUST, "trust");
        System.getProperties().setProperty(Cacerts.TRUST_PWD, "pwd");
        mojo.execute();
        MatcherAssert.assertThat(
            "trust store password cannot be in the properties",
            properties.getProperty(Cacerts.TRUST_PWD),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            "trust store location cannot be in the properties",
            properties.getProperty(Cacerts.TRUST),
            Matchers.notNullValue()
        );
    }
}
