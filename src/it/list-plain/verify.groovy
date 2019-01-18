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

def resourceNames = [
    'conf/config.test',
    'classes/module-info.class',
    'classes/myproject/HelloWorld.class',
] as Set

def buildLog = new File (basedir, 'build.log')

def listLines = buildLog.readLines()
                            .dropWhile{ !it.startsWith('[INFO] The following files are contained in the module file') }
                            .drop(1)
                            .takeWhile{ !it.startsWith('[INFO] ---') }
                            .findAll{ it.startsWith('[INFO] ')}
                            .collect{ it - '[INFO] ' } as Set

assert listLines == resourceNames
