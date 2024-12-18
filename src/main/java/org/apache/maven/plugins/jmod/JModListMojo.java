/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.plugins.jmod;

import javax.inject.Inject;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.shared.utils.cli.Commandline;
import org.apache.maven.toolchain.ToolchainManager;

/**
 * This goal is to support the usage of <code>jmod list</code> to show the content of a <code>jmod</code> file.
 *
 * @author Karl Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
@Mojo(name = "list", requiresDependencyResolution = ResolutionScope.NONE, defaultPhase = LifecyclePhase.NONE)
public class JModListMojo extends AbstractJModMojo {

    @Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
    private File outputDirectory;

    /**
     * The name of the jmod file which is examined via <code>jmod list jmodFile</code>.
     */
    // @formatter:off
    @Parameter(
            defaultValue = "${project.build.directory}/jmods/${project.artifactId}.jmod",
            property = "jmodfile",
            required = true)
    // @formatter:on
    private File jmodFile;

    @Inject
    public JModListMojo(ToolchainManager toolchainManager) {
        super(toolchainManager);
    }

    public void execute() throws MojoExecutionException, MojoFailureException {

        String jModExecutable;
        try {
            jModExecutable = getJModExecutable();
        } catch (IOException e) {
            throw new MojoFailureException("Unable to find jmod command: " + e.getMessage(), e);
        }

        getLog().info("Toolchain in maven-jmod-plugin: jmod [ " + jModExecutable + " ]");

        Commandline cmd = createJModListCommandLine();
        cmd.setExecutable(jModExecutable);

        executeCommand(cmd, outputDirectory);
    }

    private Commandline createJModListCommandLine() throws MojoFailureException {
        if (!jmodFile.exists() || !jmodFile.isFile()) {
            throw new MojoFailureException("Unable to find " + jmodFile.getAbsolutePath());
        }

        Commandline commandLine = new Commandline();
        commandLine.createArg().setValue("list");
        commandLine.createArg().setValue(jmodFile.getAbsolutePath());
        return commandLine;
    }
}
