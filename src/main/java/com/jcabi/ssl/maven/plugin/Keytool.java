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

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.log.Logger;
import com.jcabi.log.VerboseProcess;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.io.FileUtils;

/**
 * Keytool abstraction.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
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
     * Public ctor.
     * @param store The location of keystore
     * @param pwd The password
     */
    public Keytool(final File store, final String pwd) {
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
        return new VerboseProcess(this.proc("-list", "-v")).stdout();
    }

    /**
     * Generate key.
     * @throws IOException If fails
     */
    @Loggable(Loggable.DEBUG)
    public void genkey() throws IOException {
        final Process proc = this.proc(
            "-genkeypair",
            "-alias",
            Keytool.LOCALHOST,
            "-keyalg",
            "RSA",
            "-keysize",
            "2048",
            "-keypass",
            this.password
        ).start();
        final PrintWriter writer = new PrintWriter(
            new OutputStreamWriter(proc.getOutputStream())
        );
        writer.print(this.appendNewLine(Keytool.LOCALHOST));
        writer.print(this.appendNewLine("ACME Co."));
        writer.print(this.appendNewLine("software developers"));
        writer.print(this.appendNewLine("San Francisco"));
        writer.print(this.appendNewLine("California"));
        writer.print(this.appendNewLine("US"));
        writer.print(this.appendNewLine(this.createLocaleDependentYes()));
        writer.close();
        new VerboseProcess(proc).stdout();
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
        new VerboseProcess(
            this.proc(
                "-importkeystore",
                "-srckeystore",
                file.getAbsolutePath(),
                "-srcstorepass",
                pwd,
                "-destkeystore",
                this.keystore,
                "-deststorepass",
                this.password
            )
        ).stdout();
    }

    /**
     * Creates a string, which consists of string with an appended
     * platform-dependent line separator.
     * @param text Text, to which the line separator needs to be appended
     * @return Contents of text with appended line separator
     */
    private String appendNewLine(final String text) {
        return String.format("%s%s", text, NEWLINE);
    }

    /**
     * Creates a text, which represents "yes" in the language,
     * specified by the current locale.
     * @return The word "Yes" translated to the current language
     */
    private String createLocaleDependentYes() {
        return new Yes().translate(Locale.getDefault());
    }

    /**
     * Create process builder.
     * @param args Arguments
     * @return Process just created and started
     * @throws IOException If fails
     */
    private ProcessBuilder proc(final String... args) throws IOException {
        final List<String> cmds = new ArrayList<String>(args.length + 1);
        cmds.add(
            String.format(
                "%s/bin/keytool",
                System.getProperty("java.home")
            )
        );
        for (final String arg : args) {
            cmds.add(arg);
        }
        cmds.add("-storetype");
        cmds.add("jks");
        cmds.add("-noprompt");
        cmds.add("-storepass");
        cmds.add(this.password);
        cmds.add("-keystore");
        cmds.add(this.keystore);
        return new ProcessBuilder(cmds);
    }
}
