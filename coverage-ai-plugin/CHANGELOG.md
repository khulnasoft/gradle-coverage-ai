## [0.0.2] - 2023-10-11

### Added

- **Automatic Test File Generation**: The plugin now automatically generates test files for source files that lack corresponding tests. This feature significantly reduces the manual effort required to bootstrap testing and improves overall test coverage.  It intelligently detects the project's testing framework (JUnit, Spock, TestNG) and generates tests accordingly.
- **Improved Source File Matching**:  Enhanced the logic for matching source files to existing test files, ensuring more accurate pairings and reducing false positives.
- **Dependency Management**: Introduced a `DependencyHelper` class to streamline the management of project dependencies, making it easier to locate and utilize required JAR files for testing and reporting.
- **New Utility Classes**: Added several utility classes (`ModelUtility`, `FrameworkCheck`, `SourceMatchResult`) to improve code organization, modularity, and reusability.  These classes encapsulate specific functionalities, making the codebase cleaner and easier to maintain.
- **Test File Generation Feedback**: The plugin now provides more informative logging and feedback during test file generation, allowing developers to track the process and identify any potential issues.
- **Support for Multiple Test Frameworks**: Added support for detecting and using different test frameworks (JUnit3, JUnit4, JUnit5, TestNG, Spock), ensuring broader compatibility across Java projects.
- **Best Practices Documentation**: Created a comprehensive best practices document (`best_practices.md`) to guide users on effectively utilizing the plugin and adhering to recommended project structure and coding conventions.

### Changed

- **CoverageAi Refactoring**: Refactored the `CoverageAi` class to improve initialization, execution processes, and overall code structure. This refactoring enhances maintainability and sets the stage for future enhancements.
- **Enhanced Logging and Error Handling**: Improved logging and error handling mechanisms throughout the plugin, providing more detailed information for debugging and troubleshooting.
- **Updated Test Project Structure**: Updated the test project structure and resources to enhance testing coverage and demonstrate the plugin's capabilities more effectively.
- **Jacoco Reporting**: Switched to XML reporting for Jacoco, providing more structured and detailed coverage information.
- **Model Prompter Improvements**:  Improved the `ModelPrompter` to handle a wider range of responses from the language model and provide more robust test file generation.  It now uses a `ModelUtility` class for common operations like file reading and JSON extraction.