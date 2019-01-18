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

package mymodule.about.cli;

import picocli.CommandLine;

import static picocli.CommandLine.*;

@Command(
        name = "Main",
        description = "Demonstrating picocli",
        headerHeading = "Demonstration Usage:%n%n")
public class Main {

    @Option(names = {"-v", "--verbose"}, description = "Verbose output?")
    private boolean verbose;

    @Option(names = {"-f", "--file"}, description = "Path and name of file", required = true)
    private String fileName;

    @Option(names = {"-h", "--help"}, description = "Display help/usage.", help = true)
    boolean help;

    public static void main(String[] arguments) {

        final Main main = CommandLine.populateCommand(new Main(), arguments);

        if (main.help) {
            CommandLine.usage(main, System.out, CommandLine.Help.Ansi.AUTO);
        } else {
            System.out.println("The provided file path and name is " + main.fileName + " and verbosity is set to " + main.verbose);
        }
    }
}
