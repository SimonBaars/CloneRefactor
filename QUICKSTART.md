# CloneRefactor Quick Start Guide

Get started with CloneRefactor in 5 minutes!

## 1. Build the Project

```bash
# Clone the repository
git clone https://github.com/SimonBaars/CloneRefactor.git
cd CloneRefactor

# Build (requires Maven and JDK 8+)
mvn clean package -DskipTests

# Verify the build
ls -lh target/clonerefactor-1.0.jar
```

## 2. Run Your First Analysis

```bash
# Analyze the CloneRefactor codebase itself
java -jar target/clonerefactor-1.0.jar src/main/java
```

You should see output like:
```
Start parse at 10:30:45.123
DetectionResults [metrics=Metrics [
  Clone classes: 10
  Cloned Lines: 168
  Percentage Duplicated: 3.09%
  Detection time: 3585ms
  ...
]]
```

## 3. Analyze Your Own Project

```bash
# Run on any Java project
java -jar target/clonerefactor-1.0.jar /path/to/your/project/src
```

## 4. Understand the Results

Key metrics to look at:
- **Clone classes**: Number of duplicate code groups found
- **Percentage Duplicated**: How much of your code is duplicated
  - < 5%: Good ✅
  - 5-10%: Moderate ⚠️
  - > 10%: Needs attention ❌

## 5. Configure for Your Needs

Edit `src/main/resources/clonerefactor.properties`:

```properties
# Detect exact clones (easiest to refactor)
clone_type=TYPE1R

# Lower thresholds to find more clones
min_lines=3
min_tokens=10
min_clone_class_size=2

# Or raise thresholds to find only significant clones
min_lines=15
min_tokens=100
min_clone_class_size=3
```

After changing configuration, rebuild:
```bash
mvn clean package -DskipTests
```

## Next Steps

- Read [README.md](README.md) for detailed usage
- Check [USAGE_GUIDE.md](USAGE_GUIDE.md) for advanced topics
- Run tests: `mvn test`
- Explore test resources: `src/test/resources/TYPE1R/`

## Common Commands

```bash
# Build with tests
mvn clean test

# Build without tests (faster)
mvn clean package -DskipTests

# Run on a project
java -jar target/clonerefactor-1.0.jar /path/to/src

# Increase memory for large projects
java -Xmx4g -jar target/clonerefactor-1.0.jar /path/to/src
```

## Troubleshooting

**Problem**: "No clones detected"
- Lower the thresholds in `clonerefactor.properties`
- Make sure you're pointing to a directory with `.java` files

**Problem**: OutOfMemoryError
- Increase heap: `java -Xmx8g -jar ...`

**Problem**: Takes too long
- Raise thresholds to reduce comparisons
- Analyze modules separately

## Getting Help

- Check the [USAGE_GUIDE.md](USAGE_GUIDE.md) for detailed help
- Look at test examples in `src/test/resources/`
- Read the thesis PDF for theoretical background
- Open an issue on GitHub

Happy clone detecting! 🔍
