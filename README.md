# CloneRefactor

CloneRefactor is a tool that bridges the gap between clone detection and refactoring. It detects code clones using refactoring-oriented clone types and performs comprehensive context analysis on detected clones. Based on this analysis, CloneRefactor can automatically refactor a subset of the detected clones by applying transformations to the source code.

For more details, please read the included thesis document.

## Table of Contents
- [Quick Start](#quick-start)
- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Examples](#examples)
- [Understanding Results](#understanding-results)

## Quick Start

```bash
# Clone the repository
git clone https://github.com/SimonBaars/CloneRefactor.git
cd CloneRefactor

# Build the project
mvn clean package -DskipTests

# Run on a Java project
java -jar target/clonerefactor-1.0.jar /path/to/your/java/project
```

## Installation

### Prerequisites
- Java 8 or higher
- Maven 3.x

### Building from Source

```bash
# Clean and build
mvn clean compile

# Build with tests
mvn clean test

# Create executable JAR
mvn clean package
```

## Usage

### Basic Usage

The simplest way to run CloneRefactor on a Java project:

```bash
java -jar target/clonerefactor-1.0.jar /path/to/java/source
```

### Command Line Usage

```bash
# Run on project root (detects src/main/java automatically)
java -jar target/clonerefactor-1.0.jar /path/to/project

# Run with Maven
mvn exec:java -Dexec.args="/path/to/java/source"

# Use the provided script
./run.sh
```

### Using as a Library

You can also use CloneRefactor programmatically in your Java code:

```java
import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.detection.model.DetectionResults;
import java.nio.file.Paths;

public class Example {
    public static void main(String[] args) {
        // Analyze a project
        DetectionResults results = Main.cloneDetection(Paths.get("/path/to/project"));
        
        // Print results
        System.out.println(results);
        
        // Access metrics
        System.out.println("Clone classes found: " + results.getClones().size());
        System.out.println("Duplication percentage: " + 
            results.getMetrics().getAverages().get("Percentage Duplicated"));
    }
}
```

## Configuration

CloneRefactor uses a properties file for configuration. The default configuration is in `src/main/resources/clonerefactor.properties`.

### Configuration Options

```properties
# Clone Type: TYPE1, TYPE1R, TYPE2, TYPE2R, TYPE3
clone_type=TYPE1R

# Scope: ALL, METHODSONLY
scope=ALL

# Minimum thresholds for clone detection
min_statements=1
min_tokens=1
min_lines=3
min_clone_class_size=2

# Type-specific settings
max_type2_variability_percentage=10.0%
max_type3_gap_size=20.0%

# Refactoring strategy: DONOTREFACTOR, EXTRACT, INLINE
refactoring_strategy=DONOTREFACTOR

# Print progress during detection
print_progress=false
```

### Clone Types Explained

- **TYPE1**: Exact clones (identical code)
- **TYPE1R**: Refactoring-oriented Type 1 clones
- **TYPE2**: Clones with renamed identifiers/literals
- **TYPE2R**: Refactoring-oriented Type 2 clones
- **TYPE3**: Clones with statement additions/deletions (gapped clones)

### Customizing Configuration Programmatically

```java
import com.simonbaars.clonerefactor.settings.Settings;
import com.simonbaars.clonerefactor.settings.CloneType;

// Modify settings before detection
Settings.get().setCloneType(CloneType.TYPE2);
Settings.get().setMinAmountOfLines(5);
Settings.get().setMinAmountOfTokens(50);
```

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CloneContentsTest

# Run specific test method
mvn test -Dtest=CloneContentsTest#testFullMethod

# Skip tests during build
mvn package -DskipTests
```

## Examples

### Example 1: Analyze Your Own Project

```bash
java -jar target/clonerefactor-1.0.jar ~/my-java-project/src
```

### Example 2: Find Type 2 Clones with Custom Thresholds

```java
import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.settings.Settings;
import com.simonbaars.clonerefactor.settings.CloneType;
import java.nio.file.Paths;

public class CustomDetection {
    public static void main(String[] args) {
        // Configure for Type 2 clones with stricter thresholds
        Settings.get().setCloneType(CloneType.TYPE2);
        Settings.get().setMinAmountOfLines(10);
        Settings.get().setMinAmountOfTokens(100);
        Settings.get().setMinCloneClassSize(3);
        
        // Run detection
        var results = Main.cloneDetection(Paths.get(args[0]));
        
        // Print summary
        System.out.println("Found " + results.getClones().size() + " clone classes");
        System.out.println(results.getMetrics());
    }
}
```

### Example 3: Analyze CloneRefactor Itself

```bash
# Analyze the CloneRefactor source code
java -jar target/clonerefactor-1.0.jar src/main/java
```

Expected output:
```
Start parse at HH:mm:ss.SSS
DetectionResults [metrics=Metrics [
  Clone classes: 10
  Cloned Lines: 168
  Percentage Duplicated: 3.09%
  ...
]]
```

## Understanding Results

### Metrics Explained

The detection results include various metrics:

- **Clone classes**: Number of sets of duplicated code
- **Cloned Lines/Nodes/Tokens**: Amount of duplicated code
- **Percentage Duplicated**: What percentage of the codebase is duplicated
- **Detection time**: Time taken to analyze (in milliseconds)

### Location Types
- **Method Level**: Clones within methods
- **Class Level**: Clones across entire classes
- **Enum Level**: Clones in enum declarations

### Content Types
- **Partial Method**: Clone is part of a method
- **Full Method**: Entire method is cloned
- **Several Methods**: Clone spans multiple methods
- **Only Fields**: Clone is only field declarations

### Relation Types
- **Same Class**: Clones within the same class
- **Sibling**: Clones in sibling classes
- **Ancestor**: Clones in ancestor/descendant classes
- **Unrelated**: Clones in unrelated classes

## Troubleshooting

### No clones detected?

1. Check that your minimum thresholds aren't too high
2. Ensure you're pointing to a directory with `.java` files
3. Try lowering `min_clone_class_size` in the configuration

### Out of memory errors?

Increase Java heap size:
```bash
java -Xmx4g -jar target/clonerefactor-1.0.jar /path/to/project
```

## Contributing

Contributions are welcome! Please ensure tests pass before submitting pull requests:

```bash
mvn clean test
```

## License

See LICENSE file for details.
