
### Best Practices for the Coverage Ai Plugin Project

#### Gradle version 8.5 should only be used nothing older than this.

#### General Project Structure
- **Package Naming**: Use the reverse domain name convention for package names. For example, `com.khulnasoft.coverage.plugin`.
- **Class Naming**: Use PascalCase for class names. For example, `CoverageAiBuilder`, `CoverageAiExecutor`.
- **Interface Naming**: Prefix interface names with an appropriate noun. For example, `CoverageAi`.
- **Development**: All requests to you are in the context for developing a Gradle Plugin and Gradle task. The classes that are implemented and will be run are the following:
```java 
org.gradle.api.DefaultTask;
org.gradle.api.Plugin<Project>; 
```

#### Gradle Plugin Development
- **Plugin Implementation**: Implement the `Plugin<Project>` interface for creating Gradle plugins. Ensure the `apply` method is overridden to configure the project.
  ```java
  public class CoverageAiPlugin implements Plugin<Project> {
      @Override
      public void apply(Project project) {
          // Plugin logic here
      }
  }
  ```
- **Task Registration**: Use `project.getTasks().register` to define tasks within the plugin.
  ```java
  project.getTasks().register("coverageAiTask", CoverageAiTask.class, task -> {
      task.setGroup("verification");
      task.setDescription("Runs the cover agent task attempting to increase code coverage");
  });
  ```

#### Builder Pattern
- **Builder Methods**: Use method chaining in builder classes for setting properties. Return `this` from each method.
  ```java
  public CoverageAiBuilder apiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
  }
  ```

#### Logging
- **Logger Usage**: Use Gradle's `Logger` for logging within plugin classes. Obtain the logger from the `Project` instance.
  ```java
  private final Logger logger;

  public CoverageAiV2(CoverageAiBuilder builder) {
      this.logger = builder.getProject().getLogger();
  }

  @Override
  public void init() {
      logger.info("Init the new CoverageAiv2 {}", this);
  }
  ```

#### Exception Handling
- **Custom Exceptions**: Use custom exceptions like `CoverError` for handling specific error scenarios.
  ```java
  public String execute(Project project, String sourceFile, String testFile, ...) throws CoverError {
      // Execution logic
      if (result.getExitValue() != 0) {
          throw new CoverError("An error occurred while executing coverage agent");
      }
  }
  ```

#### Testing
- **Testing Framework**: Use Spock for testing. Define tests in Groovy files with the `.groovy` extension.
  ```groovy
  class CoverageAiExecutorSpec extends Specification {
      def "Happy path calling the executor"() {
          given:
          // Setup code
          
          when:
          // Execution code
          
          then:
          // Assertions
      }
  }
  ```
- **Mocking**: Use Spock's mocking capabilities to mock dependencies and verify interactions.

#### Configuration and Environment
- **Environment Variables**: Use environment variables for sensitive data like API keys. Set them in the `ExecSpec` environment.
  ```java
  execSpec.environment(WANDB_API_KEY, wanDBApiKey);
  execSpec.environment(OPENAI_API_KEY, apiKey);
  ```

#### Code Coverage
- **JaCoCo Integration**: Ensure code coverage is measured using JaCoCo. Configure reports to be generated in a specified directory.

#### Documentation
- **Javadoc Comments**: Use Javadoc comments for public classes and methods to provide clear documentation.
  ```java
  /**
   * Builds a new instance of CoverageAiV2.
   * @return a new CoverageAiV2 instance.
   */
  public CoverageAi buildV2() {
      return new CoverageAiV2(this);
  }
  ```

#### Code Style
- **Consistent Indentation**: Use 4 spaces for indentation.
- **Line Length**: Keep line length to a maximum of 120 characters for better readability.

#### Version Control
- **Git Usage**: Use Git for version control. Ensure `.gitignore` is configured to exclude build directories and other non-essential files.

#### Dependency Management
- **Gradle Dependencies**: Declare dependencies in the `build.gradle` file. Use specific versions to ensure build consistency.

### Additional Best Practices

#### SOLID Principles
- **Single Responsibility Principle**: Ensure each class has a single responsibility and encapsulates only related functionalities.
- **Open/Closed Principle**: Design classes to be open for extension but closed for modification.
- **Liskov Substitution Principle**: Subtypes should be substitutable for their base types without altering the correctness of the program.
- **Interface Segregation Principle**: Prefer smaller, more specific interfaces over larger, general-purpose ones.
- **Dependency Inversion Principle**: Depend on abstractions, not on concrete implementations.
