# CloneRefactor Usage Guide

This guide provides detailed instructions and examples for using CloneRefactor on any Java codebase.

## Table of Contents
1. [Getting Started](#getting-started)
2. [Basic Usage](#basic-usage)
3. [Advanced Configuration](#advanced-configuration)
4. [Understanding Clone Types](#understanding-clone-types)
5. [Interpreting Results](#interpreting-results)
6. [Common Use Cases](#common-use-cases)
7. [Best Practices](#best-practices)
8. [Troubleshooting](#troubleshooting)

## Getting Started

### System Requirements
- Java Development Kit (JDK) 8 or higher
- Maven 3.x (for building from source)
- At least 2GB of RAM (4GB recommended for large projects)

### Building CloneRefactor

```bash
# Clone the repository
git clone https://github.com/SimonBaars/CloneRefactor.git
cd CloneRefactor

# Build the project
mvn clean package -DskipTests

# Verify the build
ls -lh target/clonerefactor-1.0.jar
```

## Basic Usage

### Running on a Project

The most common usage pattern:

```bash
# Run on a project's source directory
java -jar target/clonerefactor-1.0.jar /path/to/project/src

# Example: Analyze an open source project
java -jar target/clonerefactor-1.0.jar ~/projects/spring-framework/spring-core/src/main/java

# Example: Analyze your own project
java -jar target/clonerefactor-1.0.jar ~/my-project/src
```

### Output Example

```
Start parse at 10:30:45.123
DetectionResults [metrics=Metrics [
  Clone classes: 25
  Cloned Lines: 450
  Percentage Duplicated: 5.2%
  Detection time: 2341ms
  ...
]]
```

## Advanced Configuration

### Using Custom Configuration File

Create a custom configuration file:

```properties
# my-config.properties
clone_type=TYPE2R
scope=ALL
min_statements=5
min_tokens=50
min_lines=10
min_clone_class_size=3
max_type2_variability_percentage=15.0%
max_type3_gap_size=25.0%
refactoring_strategy=DONOTREFACTOR
print_progress=true
```

Place this file in `src/main/resources/clonerefactor.properties` and rebuild.

### Programmatic Configuration

For more control, use the Settings API:

```java
import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.settings.Settings;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Scope;
import java.nio.file.Paths;

public class CustomAnalysis {
    public static void main(String[] args) {
        // Configure detection parameters
        Settings settings = Settings.get();
        settings.setCloneType(CloneType.TYPE2R);
        settings.setMinAmountOfLines(10);
        settings.setMinAmountOfTokens(100);
        settings.setMinCloneClassSize(3);
        settings.setPrintProgress(true);
        
        // Run detection
        var results = Main.cloneDetection(Paths.get(args[0]));
        
        // Process results
        System.out.println("Analysis complete!");
        System.out.println("Clone classes: " + results.getClones().size());
        System.out.println("Duplication: " + 
            results.getMetrics().averages.get("Percentage Duplicated") + "%");
    }
}
```

## Understanding Clone Types

### TYPE1 / TYPE1R (Exact Clones)
Identical code fragments, possibly with variations in whitespace, comments, and layout.

**Use when:** You want to find exact duplications for immediate refactoring opportunities.

```bash
# Set TYPE1R in config
clone_type=TYPE1R
```

**Example:**
```java
// Clone 1
public void processOrder(Order order) {
    validateOrder(order);
    calculateTotal(order);
    saveOrder(order);
}

// Clone 2 (identical)
public void processOrder(Order order) {
    validateOrder(order);
    calculateTotal(order);
    saveOrder(order);
}
```

### TYPE2 / TYPE2R (Renamed Clones)
Similar code with variations in identifiers, literals, and types.

**Use when:** You want to find structurally similar code with different variable names.

```bash
# Set TYPE2R in config
clone_type=TYPE2R
max_type2_variability_percentage=10.0%
```

**Example:**
```java
// Clone 1
public void processOrder(Order order) {
    validate(order);
    calculate(order);
    save(order);
}

// Clone 2 (different variable names)
public void handleRequest(Request req) {
    validate(req);
    calculate(req);
    save(req);
}
```

### TYPE3 (Gapped Clones)
Similar code with modifications including inserted/deleted statements.

**Use when:** You want to find similar code structures even with some differences.

```bash
# Set TYPE3 in config
clone_type=TYPE3
max_type3_gap_size=20.0%
```

**Example:**
```java
// Clone 1
public void processOrder(Order order) {
    validate(order);
    calculate(order);
    save(order);
}

// Clone 2 (with additional statements)
public void processOrder(Order order) {
    validate(order);
    logActivity("Processing order");  // Extra statement
    calculate(order);
    notifyUser(order);  // Extra statement
    save(order);
}
```

## Interpreting Results

### Metrics Explained

#### General Statistics
- **Clone classes**: Number of clone groups found
- **Cloned Lines/Nodes/Tokens**: Total amount of duplicated code
- **Percentage Duplicated**: What portion of your code is duplicated
- **Detection time**: Analysis duration in milliseconds

#### Location Types
- **Method Level**: Clones within or across methods
- **Class Level**: Clones at class scope
- **Enum Level**: Clones in enum declarations

#### Content Types
- **Partial Method**: Clone is part of a method body
- **Full Method**: Entire method is cloned
- **Several Methods**: Clone spans multiple methods
- **Only Fields**: Clone contains only field declarations

#### Relation Types
- **Same Class**: Clones within the same class
- **Sibling**: Clones in sibling classes (same parent)
- **Ancestor**: Clones in classes with inheritance relationship
- **Unrelated**: Clones in unrelated classes

### Example Output Analysis

```
Clone classes: 15
Cloned Lines: 320
Percentage Duplicated: 4.2%
```

**Interpretation:**
- 15 groups of duplicate code were found
- 320 lines total are involved in duplication
- 4.2% of your codebase is duplicated
- This is a moderate level of duplication (< 5% is good, > 10% needs attention)

## Common Use Cases

### Use Case 1: Quick Project Health Check

Find major duplication issues quickly:

```bash
# Use default settings for quick scan
java -jar target/clonerefactor-1.0.jar ~/my-project/src | grep "Percentage Duplicated"
```

### Use Case 2: Pre-Refactoring Analysis

Before a major refactoring, identify all similar code:

```java
Settings.get().setCloneType(CloneType.TYPE2R);
Settings.get().setMinAmountOfLines(5);
Settings.get().setMinCloneClassSize(2);

var results = Main.cloneDetection(Paths.get(projectPath));

// Export results for review
System.out.println(results.sorted());
```

### Use Case 3: Code Review Tool

Integrate into code review process:

```bash
# Analyze feature branch
git checkout feature/new-feature
java -jar clonerefactor.jar src > clone-report-new.txt

# Compare with main branch
git checkout main
java -jar clonerefactor.jar src > clone-report-main.txt

# Review differences
diff clone-report-main.txt clone-report-new.txt
```

### Use Case 4: Continuous Monitoring

Track duplication over time:

```bash
#!/bin/bash
# monitor-clones.sh

DATE=$(date +%Y-%m-%d)
java -jar clonerefactor.jar src | grep "Percentage Duplicated" >> duplication-history.log
echo "$DATE: Duplication tracked" >> duplication-history.log
```

### Use Case 5: Large Project Analysis

For projects with 100k+ lines:

```bash
# Increase heap size
java -Xmx8g -jar target/clonerefactor-1.0.jar ~/large-project/src

# Use stricter thresholds to reduce noise
# Edit clonerefactor.properties:
# min_lines=20
# min_tokens=200
# min_clone_class_size=3
```

## Best Practices

### 1. Start with Conservative Thresholds

```properties
# Good starting point
min_lines=10
min_tokens=50
min_clone_class_size=3
```

### 2. Adjust Based on Project Size

**Small projects (< 10k LOC):**
```properties
min_lines=5
min_tokens=25
min_clone_class_size=2
```

**Large projects (> 100k LOC):**
```properties
min_lines=20
min_tokens=100
min_clone_class_size=3
```

### 3. Focus on Refactorable Clones

Start with TYPE1R clones as they're easiest to refactor:
```properties
clone_type=TYPE1R
```

### 4. Iterate and Refine

1. Run analysis with default settings
2. Review results
3. Adjust thresholds if too many/few results
4. Focus on high-impact clones first

### 5. Document Your Configuration

Keep a project-specific configuration:
```bash
# Save your config
cp src/main/resources/clonerefactor.properties docs/clone-detection-config.properties

# Add to version control
git add docs/clone-detection-config.properties
```

## Troubleshooting

### Problem: No Clones Detected

**Symptoms:** Analysis completes but reports 0 clone classes.

**Solutions:**
1. Lower the thresholds:
   ```properties
   min_lines=3
   min_tokens=10
   min_clone_class_size=2
   ```

2. Verify you're analyzing the right directory:
   ```bash
   # Make sure this directory contains .java files
   ls -R /path/to/analyze | grep .java
   ```

3. Check clone type matches your expectations:
   ```properties
   # Try TYPE1R first
   clone_type=TYPE1R
   ```

### Problem: Too Many False Positives

**Symptoms:** Many irrelevant clones reported (getters, setters, etc.).

**Solutions:**
1. Increase minimum thresholds:
   ```properties
   min_lines=15
   min_tokens=100
   ```

2. Filter out trivial methods manually
3. Focus on specific clone types

### Problem: OutOfMemoryError

**Symptoms:** Java heap space error during analysis.

**Solutions:**
1. Increase heap size:
   ```bash
   java -Xmx8g -jar target/clonerefactor-1.0.jar /path/to/project
   ```

2. Analyze subdirectories separately:
   ```bash
   java -jar clonerefactor.jar project/module1/src
   java -jar clonerefactor.jar project/module2/src
   ```

### Problem: Analysis Takes Too Long

**Symptoms:** Detection runs for more than 30 minutes.

**Solutions:**
1. Increase thresholds to reduce comparisons
2. Exclude test directories if not needed
3. Analyze modules separately

### Problem: Path Contains Spaces

**Symptoms:** Error when path has spaces.

**Solutions:**
```bash
# Use quotes
java -jar target/clonerefactor-1.0.jar "/path/with spaces/src"
```

## Integration Examples

### Maven Integration

Add to your `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>3.0.0</version>
            <executions>
                <execution>
                    <id>clone-detection</id>
                    <phase>verify</phase>
                    <goals>
                        <goal>java</goal>
                    </goals>
                    <configuration>
                        <mainClass>com.simonbaars.clonerefactor.Main</mainClass>
                        <arguments>
                            <argument>src/main/java</argument>
                        </arguments>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Run with:
```bash
mvn verify
```

### CI/CD Integration (GitHub Actions)

```yaml
name: Clone Detection

on: [push, pull_request]

jobs:
  detect-clones:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
          distribution: 'adopt'
      
      - name: Run Clone Detection
        run: |
          # Clone and build CloneRefactor
          git clone https://github.com/SimonBaars/CloneRefactor.git /tmp/cr
          cd /tmp/cr
          mvn clean package -DskipTests
          
          # Run on project
          cd $GITHUB_WORKSPACE
          java -jar /tmp/cr/target/clonerefactor-1.0.jar src/main/java > clone-report.txt
          
          # Check threshold
          DUPLICATION=$(grep "Percentage Duplicated" clone-report.txt | grep -oP '\d+\.\d+')
          if (( $(echo "$DUPLICATION > 10.0" | bc -l) )); then
            echo "⚠️  Duplication is ${DUPLICATION}% (threshold: 10%)"
            exit 1
          fi
```

## Support and Resources

- **GitHub Issues**: Report bugs or request features
- **Thesis Document**: See `Master_Thesis_Simon_Baars.pdf` for theoretical background
- **Source Code**: Browse the code for implementation details
- **Test Cases**: Check `src/test/resources/` for examples of detectable clones

## Additional Tips

1. **Start Small**: Test on a single module before running on entire project
2. **Review Regularly**: Run clone detection monthly or quarterly
3. **Set Realistic Goals**: Aim for < 5% duplication gradually
4. **Prioritize**: Focus on clones in critical/complex code first
5. **Document Decisions**: Note why certain clones are acceptable

For more information, see the main README.md file.
