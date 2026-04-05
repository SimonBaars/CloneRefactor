# AGENTS.md

## Cursor Cloud specific instructions

**Product:** CloneRefactor — a Java CLI tool that detects code clones (Type 1/2/3) in Java projects using AST-based analysis (JavaParser) and can automatically refactor a subset of detected clones.

**Build system:** Maven 3.x with JDK 21 (project targets Java 8 source level, compatible with JDK 21).

### Build / Test / Run commands

| Action | Command |
|--------|---------|
| Compile | `mvn clean compile` |
| Test | `mvn test` |
| Package (JAR) | `mvn clean package -DskipTests` |
| Run CLI | `java -cp "target/clonerefactor-1.0.jar:$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout)" com.simonbaars.clonerefactor.Main <path-to-java-project>` |

### Non-obvious notes

- **Pre-existing test failures:** The test suite (72 tests) has pre-existing failures (3 failures, 54 errors) on `master`. The `Type1Test`, `Type2Test`, `Type3Test` helper classes fail because they contain no JUnit 3 test methods. The `CloneRelationTest` errors are `NoSuchElementException` bugs in test code (not environment-related).
- **`mvn exec:java` uses `RunOnCorpus`:** The `pom.xml` configures `exec-maven-plugin` to run `RunOnCorpus` (batch corpus analysis), not `Main`. To run the CLI entry point, invoke `Main` directly via `java -cp` as shown above.
- **Output directory:** The tool writes results to `~/clone/output/<timestamp>/`. Ensure `~/clone/output/` exists or let the tool create it.
- **JVM config:** `.mvn/jvm.config` sets `-Xms2G -Xmx10G -Xss20m`. This applies to Maven-invoked tasks but not to direct `java -cp` invocations (add these flags manually if needed for large projects).
- **No lint tool configured:** There is no dedicated linter; `mvn compile` serves as the primary static check.
