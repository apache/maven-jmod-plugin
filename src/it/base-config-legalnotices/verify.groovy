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

import java.util.jar.*

def target = new File( basedir, 'target' )
assert ( target.exists() && target.isDirectory() ) : 'target file is missing or not a directory.'

def artifact = new File( target, 'jmods/maven-jmod-plugin-base-config-legalnotices.jmod' )
assert ( artifact.exists() && artifact.isFile() ) : 'target file is missing or a directory.'

def resourceNames = [
    'legal/first.md',
    'classes/module-info.class',
    'classes/myproject/HelloWorld.class',
] as Set

def contents = [] as Set

def jar = new JarFile( artifact )
def jarEntries = jar.entries()
while ( jarEntries.hasMoreElements() ) {
    def entry = (JarEntry) jarEntries.nextElement()
    if ( !entry.isDirectory() ) {
        // Only compare files
        contents.add( entry.getName() )
    }
}

assert resourceNames == contents
