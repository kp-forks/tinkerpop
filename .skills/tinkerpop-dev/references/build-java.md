<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

# Java / Core Module Builds

## Building Specific Modules

Build a single module:
```bash
mvn clean install -pl <module-name>
```

Examples:
```bash
mvn clean install -pl tinkergraph-gremlin
mvn clean install -pl gremlin-core
mvn clean install -pl gremlin-server
```

Build a module and its dependencies:
```bash
mvn clean install -pl gremlin-server -am
```

## Gremlin Server

Build gremlin-server (commonly needed before GLV testing):
```bash
mvn clean install -pl :gremlin-server -am -DskipTests
```

Run gremlin-server integration tests:
```bash
mvn clean install -pl gremlin-server -DskipIntegrationTests=false
```

Start Gremlin Server with Docker using the standard test configuration:
```bash
docker/gremlin-server.sh
```

## Integration Tests

Enable integration tests across the project:
```bash
mvn clean install -DskipIntegrationTests=false
```

Use the `-DuseEpoll` option to try Netty native transport (Linux only; falls back to
Java NIO on other OS).

## Test Options

Disable iterator leak assertions:
```bash
mvn clean install -DtestIteratorLeaks=false
```

Specify a test seed for reproducible `Random` behavior:
```bash
mvn clean install -DtestSeed
```
When a test fails, the seed is printed in build output (look for "TestHelper" logger).
Re-run with the same seed to reproduce.

Mute heavy logging in process tests:
```bash
mvn clean install -DargLine="-DmuteTestLogs=true"
```

Run specific tests in a TinkerPop Suite:
```bash
export GREMLIN_TESTS='org.apache.tinkerpop.gremlin.process.traversal.step.map.MatchTest$CountMatchTraversals'
mvn -Dmaven.javadoc.skip=true --projects tinkergraph-gremlin test
```

## Docker Images

Build Docker images of Gremlin Server and Console:
```bash
mvn clean install -pl gremlin-server,gremlin-console -DdockerImages
```

Skip automatic Docker image build:
```bash
mvn clean install -DskipImageBuild
```

## Other Useful Commands

Check license headers:
```bash
mvn apache-rat:check
```

Check for newer dependencies:
```bash
mvn versions:display-dependency-updates
mvn versions:display-plugin-updates
```

Test coverage report (must use `install`, not `test`):
```bash
mvn clean install -Dcoverage
```
Reports go to `gremlin-tools/gremlin-coverage/target/site`.

Benchmarks:
```bash
mvn verify -DskipBenchmarks=false
```
Reports go to `gremlin-tools/gremlin-benchmark/target/reports/benchmark`.

Regenerate toy graph data (only after IO class changes):
```bash
# From tinkergraph-gremlin directory
mvn clean install -Dio
```
