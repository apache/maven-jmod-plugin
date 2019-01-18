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

def validateArtifact( String module, List<String> artifactNames )
{
    println( "Checking if ${basedir}/${module}/target exists." )
    File target = new File( basedir, "/${module}/target" )
    assert target.isDirectory()

    File artifact = new File( target, "/jmods/myproject.${module}.jmod" )
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

    assert artifactNames.size() == contents.size()

    artifactNames.each{ artifactName ->
        assert contents.contains( artifactName )
    }
}

validateArtifact( "world", [ "classes/module-info.class", "classes/myproject/world/World.class" ] )
validateArtifact( "greetings", [ "classes/module-info.class", "classes/myproject/greetings/Main.class" ] )

def sout = new StringBuilder(), serr = new StringBuilder()
def proc = "jmod describe ${basedir}/greetings/target/jmods/myproject.greetings.jmod".execute()
proc.consumeProcessOutput( sout, serr )
proc.waitForOrKill( 1000 )
if ( ! sout.toString().trim().isEmpty() && serr.toString().trim().isEmpty() )
{
    Set<String> expectedLines = new HashSet(
            Arrays.asList(
                    "myproject.greetings@99.0",
                    "requires java.base mandated",
                    "requires myproject.world",
                    "contains myproject.greetings",
                    "main-class myproject.greetings.Main" ) )
    String[] lines = sout.toString().split("\n")
    for ( String line : lines )
    {
        if ( ! line.trim().isEmpty() && !expectedLines.contains(line) )
        {
            System.err.println( "This line was not returned from jmod: ${line}" )
            return false
        }
        else
        {
            expectedLines.remove(line)
        }
    }
    if (!expectedLines.isEmpty()) {
        System.err.println( "This module does not the following items:" )
        for ( String line : expectedLines )
        {
            System.err.println( line )
        }
        return false
    }
}
else
{
    System.err.println( "Some error happened while trying to run 'jmod describe "
            + "${target}/jmods/myproject.greetings.jmod'" )
    System.err.println( serr )
    return false
}