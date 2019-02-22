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

import java.util.jar.JarEntry
import java.util.jar.JarFile

validateArtifact( "world", "/myproject.world-99.0.jar",
        [ "module-info.class",
          "META-INF/maven/org.apache.maven.plugins/myproject.world/pom.xml",
          "myproject/world/World.class",
          "META-INF/maven/org.apache.maven.plugins/myproject.world/pom.properties",
          "META-INF/MANIFEST.MF"
        ]
)
validateArtifact( "greetings", "/jmods/myproject.greetings.jmod",
        [ "classes/module-info.class",
          "classes/myproject/greetings/Main.class" ] )

def buildLog = new File(basedir,'build.log')

def describeLines = buildLog.readLines()
                            .dropWhile{ it != '[INFO] myproject.greetings@99.0' } // start line, inclusive
                            .takeWhile{ !it.startsWith('[INFO] ---') }            // end line, inclusive
                            .grep()                                               // remove empty lines
                            .collect{ it - '[INFO] ' } as Set                        // strip loglevel

def expectedLines = [
                "myproject.greetings@99.0",
                "requires java.base mandated",
                "requires myproject.world",
                "contains myproject.greetings",
                "main-class myproject.greetings.Main"] as Set

assert describeLines == expectedLines

def validateArtifact(module, String filename, resourceNames)
{
    println( "Checking if ${basedir}/${module}/target exists." )
    def targetDir = new File( basedir, "/${module}/target" )
    assert targetDir.isDirectory()

    File artifact = new File( targetDir, filename )
    assert artifact.isFile()

    Set contents = new HashSet()

    JarFile jar = new JarFile( artifact )
    Enumeration jarEntries = jar.entries()
    while ( jarEntries.hasMoreElements() )
    {
        JarEntry entry = (JarEntry) jarEntries.nextElement()
        println( "Current entry: ${entry}" )
        if ( !entry.isDirectory() )
        {
            // Only compare files
            contents.add( entry.getName() )
        }
    }

    assert resourceNames.size() == contents.size()

    resourceNames.each{ artifactName ->
        assert contents.contains( artifactName )
    }
}
