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

# Documentation

## Format and Location

TinkerPop documentation is AsciiDoc-based and lives under `docs/src/`:

```
docs/src/
├── reference/          Main reference documentation
├── dev/
│   ├── developer/      Developer guides (environment, contributing, releases)
│   ├── provider/       Graph provider documentation, Gremlin semantics
│   ├── io/             IO and serialization formats
│   └── future/         Future plans
├── recipes/            Gremlin recipes and patterns
├── tutorials/          Getting started tutorials
└── upgrade/            Version upgrade guides
```

Do not use Markdown in the main docs tree. Use AsciiDoc.

## Adding or Updating Documentation

1. Place new content in the appropriate book (reference, dev, recipes, etc.).
2. Update the relevant `index.asciidoc` so the new content is included in the build.
3. Follow existing patterns for section structure and formatting.

## Generating Documentation

Documentation generation requires:
- Java 11 (specifically for doc generation)
- Hadoop 3.3.x in pseudo-distributed mode
- GNU versions of `grep`, `awk` (4.0.1+), `sed`, `findutils`, `diffutils`
  (on macOS, install via Homebrew)
- `zip` utility

Generate documentation:
```bash
bin/process-docs.sh
```
Output goes to `target/docs/`.

Dry run (don't evaluate code blocks):
```bash
bin/process-docs.sh --dryRun
```

Dry run for specific files:
```bash
bin/process-docs.sh --dryRun docs/src/reference/the-graph.asciidoc,docs/src/tutorial/getting-started,...
```

Full run for specific files only:
```bash
bin/process-docs.sh --fullRun docs/src/reference/the-graph.asciidoc,...
```

Process a single file:
```bash
docs/preprocessor/preprocess-file.sh \
  $(pwd)/gremlin-console/target/apache-tinkerpop-gremlin-console-*-standalone \
  "" "*" $(pwd)/docs/src/xyz.asciidoc
```

Documentation can also be generated with Docker:
```bash
docker/build.sh -d
```

## Website Generation

Generate the website locally (no Hadoop or special infrastructure needed):
```bash
bin/generate-home.sh
```
Output goes to `target/site/home`.

The `docs/gremlint` web app requires Node.js and npm to be installed locally for
website generation and publishing.

## JavaDoc / JSDoc

Generate JavaDocs and JSDoc:
```bash
mvn process-resources -Djavadoc
```
- JavaDoc output: `target/site/apidocs/`
- JSDoc output: `gremlin-js/gremlin-javascript/doc/`

Use Java 11 for JavaDoc generation.

## Publishing

Deploy documentation:
```bash
bin/publish-docs.sh svn-username
```

Publish website:
```bash
bin/publish-home.sh <username>
```

## Common Issues

- **"Error grabbing grapes"** during plugin installation: delete the problematic
  dependency directories from both `~/.m2/` and `~/.groovy/grapes/`.
- **Unexpected OLAP failures**: usually a jar conflict from modified Hadoop/Spark
  dependencies. Check `DependencyGrabber` class and plugin loading paths.
- **awk version**: version 4.0.1 is required.
