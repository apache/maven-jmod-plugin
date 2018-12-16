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


try {
    println("Checking if ${basedir}/world/target exists.")
    File target = new File( basedir, "/world/target" )
    if ( !target.exists() || !target.isDirectory() ) {
        System.err.println( "${target.getAbsolutePath()} folder is missing or not a directory." )
        return false
    }

    File artifact = new File( target, "/jmods/myproject.world.jmod" )
    if ( !artifact.exists() || artifact.isDirectory() ) {
        System.err.println( "${artifact.getAbsolutePath()} file is missing or a directory." )
        return false
    }

    String[] artifactNames = [
            "classes/module-info.class",
            "classes/myproject/world/World.class"
    ]

    Set contents = new HashSet()

    JarFile jar = new JarFile( artifact )
    Enumeration jarEntries = jar.entries()
    while ( jarEntries.hasMoreElements() ) {
        JarEntry entry = (JarEntry) jarEntries.nextElement()
        println("Current entry: ${entry}")
        if ( !entry.isDirectory() ) {
            // Only compare files
            contents.add( entry.getName() )
        }
    }

    if  ( artifactNames.length != contents.size() ) {
        System.err.println( "jar content size is different from the expected content size" )
        return false
    }
    artifactNames.each{ artifactName ->
        if ( !contents.contains( artifactName ) ) {
            System.err.println( "Artifact[" + artifactName + "] not found in jar archive" )
            return false
        }
    }
} catch ( Throwable e ) {
    e.printStackTrace()
    return false
}

try {
    println("Checking if ${basedir}/greetings/target exists.")
    File target = new File( basedir, "/greetings/target" )
    if ( !target.exists() || !target.isDirectory() ) {
        System.err.println( "${target.getAbsolutePath()} folder is missing or not a directory." )
        return false
    }

    File artifact = new File( target, "/jmods/myproject.greetings.jmod" )
    if ( !artifact.exists() || artifact.isDirectory() ) {
        System.err.println( "${artifact.getAbsolutePath()} file is missing or a directory." )
        return false
    }

    String[] artifactNames = [
            "classes/module-info.class",
            "classes/myproject/greetings/Main.class"
    ]

    Set contents = new HashSet()

    JarFile jar = new JarFile( artifact )
    Enumeration jarEntries = jar.entries()
    while ( jarEntries.hasMoreElements() ) {
        JarEntry entry = (JarEntry) jarEntries.nextElement()
        println("Current entry: ${entry}")
        if ( !entry.isDirectory() ) {
            // Only compare files
            contents.add( entry.getName() )
        }
    }

    if  ( artifactNames.length != contents.size() ) {
        System.err.println( "jar content size is different from the expected content size" )
        return false
    }
    artifactNames.each{ artifactName ->
        if ( !contents.contains( artifactName ) ) {
            System.err.println( "Artifact[" + artifactName + "] not found in jar archive" )
            return false
        }
    }

    def sout = new StringBuilder(), serr = new StringBuilder()
    def proc = "jmod describe ${target}/jmods/myproject.greetings.jmod".execute()
    proc.consumeProcessOutput(sout, serr)
    proc.waitForOrKill(1000)
    if (!sout.toString().trim().isEmpty() && serr.toString().trim().isEmpty()) {
        Set<String> expectedLines = new HashSet(Arrays.asList("myproject.greetings@99.0",
                "requires java.base mandated",
                "requires myproject.world",
                "contains myproject.greetings",
                "main-class myproject.greetings.Main"))
        String[] lines = sout.toString().split("\n")
        for (String line : lines) {
            if (!line.trim().isEmpty() && !expectedLines.contains(line)) {
                System.err.println( "This line was not returned from jmod: ${line}" )
                return false
            } else {
                expectedLines.remove(line)
            }
        }
        if (!expectedLines.isEmpty()) {
            System.err.println( "This module does not the following items:" )
            for (String line : expectedLines) {
                System.err.println( line )
            }
            return false
        }
        return true
    } else {
        System.err.println( "Some error happened while trying to run 'jmod describe "
                + "${target}/jmods/myproject.greetings.jmod'" )
        System.err.println(serr)
        return false
    }
} catch ( Throwable e ) {
    e.printStackTrace()
    return false
}

return true