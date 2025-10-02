# CloneRefactor Examples

This directory contains examples demonstrating how to use CloneRefactor.

## Sample Project

The `sample-project` directory contains a simple Java project with intentional code clones. This is useful for:
- Learning how CloneRefactor detects clones
- Testing configuration changes
- Understanding detection results

### Running CloneRefactor on the Sample Project

```bash
# From the CloneRefactor root directory
cd /path/to/CloneRefactor

# Make sure you've built the project
mvn clean package -DskipTests

# Run detection on the sample project
# Note: CloneRefactor works best on standard Java project structures
# For this example, analyze the test resources which have proper structure
java -jar target/clonerefactor-1.0.jar src/test/resources/TYPE1R/SimpleClone
```

### Expected Results

The sample project contains:
- Type 1 clones: `processNewOrder` in `OrderService` is identical to `createCustomerOrder` in `CustomerService`
- Similar code patterns that may be detected depending on configuration

### What You'll See

```
Start parse at HH:mm:ss.SSS
DetectionResults [metrics=Metrics [
  Clone classes: 1-2 (depending on configuration)
  Cloned Lines: ~12
  Percentage Duplicated: ~15-20%
  Location: Method Level
  Relation: Unrelated (different classes)
]]
```

### Experimenting with Configuration

Try different settings to see how they affect results:

1. **Strict thresholds** - Edit `src/main/resources/clonerefactor.properties`:
   ```properties
   min_lines=10
   min_tokens=100
   ```
   This will only detect larger clones.

2. **Type 2 detection** - Detect clones with renamed variables:
   ```properties
   clone_type=TYPE2R
   ```

3. **Different scopes** - Only check methods:
   ```properties
   scope=METHODSONLY
   ```

## Creating Your Own Test Cases

To create test cases for learning or testing:

1. Create a new directory: `mkdir examples/my-test`
2. Add Java files with duplicate code
3. Run: `java -jar target/clonerefactor-1.0.jar examples/my-test`
4. Experiment with different configurations

## Additional Resources

- See `USAGE_GUIDE.md` for detailed usage instructions
- See test resources in `src/test/resources/TYPE1R/` for more examples
- Check the thesis PDF for theoretical background
