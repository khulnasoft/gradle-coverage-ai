package com.khulnasoft.coverage.plugin;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O;

import com.google.gson.Gson;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileTree;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.api.tasks.compile.JavaCompile;

public class CoverageAi {
  private static final int MAX_TOKENS = 500;
  private static final String PATH_SEPARATOR = System.getProperty("path.separator");
  private static final String JAVAC_COMMAND = "javac ";
  private final List<String> javaSourceDir = new ArrayList<>();
  private final List<File> javaSourceFiles = new ArrayList<>();
  private final List<File> javaTestSourceFiles = new ArrayList<>();
  private final String apiKey;
  private final String wanDBApiKey;
  private final int iterations;
  private final int coverage;
  private final String coverageAiBinaryPath;
  private final Project project;
  private final Logger logger;
  private final OpenAiChatModel.OpenAiChatModelBuilder openAiChatModelBuilder;
  private final Map<File, String> testToSourceFileMatches = new HashMap<>();
  private ModelPrompter modelPrompter;
  private Optional<String> javaClassPath = Optional.empty();
  private Optional<String> javaTestClassPath = Optional.empty();
  private String projectPath;
  private Optional<String> javaClassDir = Optional.empty();
  private String buildDirectory;
  private CoverageAiExecutor coverageAiExecutor;
  private Optional<String> javaCompileCommand = Optional.empty();
  private Optional<String> javaTestCompileCommand = Optional.empty();
  private DependencyHelper dependencyHelper;
  public CoverageAi(CoverageAiBuilder builder) {
    this.apiKey = builder.getApiKey();
    this.wanDBApiKey = builder.getWanDBApiKey();
    this.iterations = builder.getIterations();
    this.coverage = builder.getCoverage();
    this.coverageAiBinaryPath = builder.getCoverageAiBinaryPath();
    this.modelPrompter = builder.getModelPrompter();
    this.javaClassPath = builder.getJavaClassPath();
    this.javaTestClassPath = builder.getJavaTestClassPath();
    this.projectPath = builder.getProjectPath();
    this.javaClassDir = builder.getJavaClassDir();
    this.buildDirectory = builder.getBuildDirectory();
    this.coverageAiExecutor = builder.getCoverageAiExecutor();
    this.project = builder.getProject();
    this.logger = project.getLogger();
    this.openAiChatModelBuilder = builder.openAiChatModelBuilder();
    this.dependencyHelper = new DependencyHelper(this.project, this.logger);
  }

  public void init() {
    initModelPrompter();
    initExecutor();
    initDirectories();
    initClassPathSources();
    if (generateMissingTestFiles()) {
      initClassPathSources();
    }
  }

  public void initClassPathSources() {
    setJavaClassPath();
    setJavaTestClassPath();
    javaCompileCommand();
    testJavaCompileCommand();
  }


  private boolean generateMissingTestFiles() {
    String framework = detectTestFramework(project);
    logger.debug("Framework to use is {}", framework);

    SourceMatchResult result = findMatchingSourceFiles(javaTestSourceFiles);
    testToSourceFileMatches.putAll(result.testToSourceMap());
    //Map<File, String> completeMapping = result.testToSourceMap();
    for (File sourceFile : result.remainingSourceFiles()) {
      processTestFileGeneration(sourceFile, framework, testToSourceFileMatches);
    }
    return !result.remainingSourceFiles().isEmpty();
  }


  private void setJavaTestClassPath() {
    JavaCompile javaTestCompileTask = project.getTasks().withType(JavaCompile.class).findByName("compileTestJava");
    if (javaTestCompileTask != null) {
      javaTestClassPath = java.util.Optional.of(buildClassPath(javaTestCompileTask));
      FileTree sourceFiles = javaTestCompileTask.getSource();
      Set<File> sourceDirs = sourceFiles.getFiles();
      javaTestSourceFiles.addAll(sourceDirs);
    }
  }

  private String buildClassPath(JavaCompile javaCompileTask) {
    StringBuilder builder = new StringBuilder();
    builder.append(javaCompileTask.getDestinationDirectory().get().getAsFile().getAbsolutePath()).append(":");
    for (File clFile : javaCompileTask.getClasspath().getFiles()) {
      builder.append(clFile.getAbsolutePath()).append(PATH_SEPARATOR);
      logger.debug("FILE {} - {}", javaCompileTask, clFile.getAbsolutePath());
    }
    logger.debug("{} path is {}", javaCompileTask, builder);
    return builder.toString();
  }

  private void setJavaClassPath() {
    JavaCompile javaCompileTask = project.getTasks().withType(JavaCompile.class).findByName("compileJava");
    if (javaCompileTask != null) {
      javaClassPath = java.util.Optional.of(buildClassPath(javaCompileTask));
      File classesDir = javaCompileTask.getDestinationDirectory().get().getAsFile();
      javaClassDir = java.util.Optional.of(classesDir.getAbsolutePath());
      FileTree sourceFiles = javaCompileTask.getSource();
      Set<File> sourceDirs = sourceFiles.getFiles();
      for (File srcDir : sourceDirs) {
        javaSourceDir.add(srcDir.getAbsolutePath());
        javaSourceFiles.add(srcDir);
      }
    }
  }

  private void initExecutor() {
    coverageAiExecutor = new CoverageAiExecutor.Builder().coverageAiBinaryPath(coverageAiBinaryPath).apiKey(apiKey)
        .wanDBApiKey(wanDBApiKey).coverage(coverage).iterations(iterations).build();
  }

  private void initModelPrompter() {
    ChatLanguageModel model =
        openAiChatModelBuilder.apiKey(this.apiKey).modelName(GPT_4_O).maxTokens(MAX_TOKENS).build();
    this.modelPrompter = new ModelPrompter(logger, model, new ModelUtility(logger));
  }

  private void initDirectories() {
    Directory projectDirectory = project.getLayout().getProjectDirectory();
    projectPath = projectDirectory.getAsFile().getAbsolutePath();
    logger.debug("Root Project path {}", projectPath);
    File buildDir = project.getLayout().getBuildDirectory().getAsFile().get();
    if (!buildDir.exists()) {
      boolean created = buildDir.mkdirs();
      if (created) {
        logger.debug("Build directory created: {}", buildDir.getAbsolutePath());
      } else {
        logger.error("Failed to create build directory: {}", buildDir.getAbsolutePath());
      }
    } else {
      logger.debug("Build directory already exists: {}", buildDir.getAbsolutePath());
    }
    buildDirectory = buildDir.getAbsolutePath();
  }

  private void deleteFileIfExists(String filePath) {
    Path path = Paths.get(filePath);
    try {
      if (Files.exists(path)) {
        Files.delete(path);
        logger.info("Deleted file: {}", filePath);
      }
    } catch (IOException e) {
      logger.error("Failed to delete file: {}", filePath, e);
    }
  }

  private String convertListToString(List<String> list) {
    return String.join(PATH_SEPARATOR, list);
  }

  private String jacocoJavaReport(String reportPath, String execPath) throws CoverError {
    List<String> jars = dependencyHelper.findNeededJars("org.jacoco:org.jacoco.cli:0.8.12");
    String jarPath = convertListToString(jars);
    String sourcePath = convertListToString(javaSourceDir);
    String classFiles = "";
    if (javaClassDir.isPresent()) {
      classFiles = javaClassDir.get();
    }
    return "java -cp " + jarPath + " org.jacoco.cli.internal.Main report " + execPath + " --classfiles " + classFiles
        + " --sourcefiles " + sourcePath + " --xml " + reportPath;
  }

  private String javaAgentCommand(String jacocExecPath) throws CoverError {
    String standAloneJunit = dependencyHelper
        .findNeededJars("org.junit.platform:junit-platform-console-standalone:1.11.0").get(0);
    String jacocoAgent = dependencyHelper.findNeededJars("org.jacoco:org.jacoco.agent:0.8.11:runtime").get(0);
    String builder = "";
    if (javaTestClassPath.isPresent()) {
      builder = "java -javaagent:" + jacocoAgent + "=destfile=" + jacocExecPath + " -cp " + standAloneJunit + ":"
          + javaTestClassPath.get() + " org.junit.platform.console.ConsoleLauncher --scan-class-path ";
    } else {
      logger.error("Java test class path not found will not work to assign agent");
    }
    return builder;
  }

  public void javaCompileCommand() {
    JavaCompile javaCompileTask = project.getTasks().withType(JavaCompile.class).findByName("compileJava");
    StringBuilder builder = new StringBuilder();
    if (javaClassPath.isPresent()) {
      builder.append(JAVAC_COMMAND);
      CompileOptions options = javaCompileTask.getOptions();
      for (String arg : options.getAllCompilerArgs()) {
        builder.append(arg).append(" ");
      }
      builder.append("-d ").append(javaCompileTask.getDestinationDirectory().get().getAsFile().getAbsolutePath())
          .append(" ");
      builder.append("-classpath ");
      builder.append(javaClassPath.get());
      builder.append(" ");
      for (File sourceFile : javaCompileTask.getSource().getFiles()) {
        builder.append(sourceFile.getAbsolutePath()).append(" ");
      }
      javaCompileCommand = Optional.ofNullable(builder.toString());
    } else {
      logger.error("No Java class path provided");
    }
  }

  private String detectTestFramework(Project project) {
    try {
      ConfigurationContainer container = project.getConfigurations();
      Configuration testConfiguration = container.getByName("testImplementation");
      DependencySet dependencies = testConfiguration.getDependencies();


      List<FrameworkCheck> frameworkChecks =
          List.of(new FrameworkCheck("spockframework"), new FrameworkCheck("testng"), new FrameworkCheck("junit5"),
              new FrameworkCheck("junit4", "4."), new FrameworkCheck("junit3", "3."));

      Optional<String> detectedFramework =
          frameworkChecks.stream().filter(check -> dependencies.stream().anyMatch(d -> check.matches(d)))
              .map(FrameworkCheck::getFramework).findFirst();

      detectedFramework.ifPresent(framework -> logger.debug(framework));
      return detectedFramework.orElse("junit5");
    } catch (Exception e) {
      logger.error("Failure in determining testing framework returning Junit5");
      return "junit5";
    }
  }

  public void invoke() {
    logger.debug("Path to coverageAiBinaryPath {}", coverageAiBinaryPath);
    executeTestsWithCoverage(testToSourceFileMatches);
  }

  private void processTestFileGeneration(File sourceFile, String framework, Map<File, String> completeMapping) {
    try {
      TestFileResponse response = modelPrompter.generateTestFile(sourceFile, framework);
      logger.info("Output the response {}", new Gson().toJson(response));
      File testFile = createTestFile(response);
      completeMapping.put(testFile, sourceFile.getAbsolutePath());
    } catch (CoverError e) {
      logger.error("Failed to generate test file for source file {}", sourceFile, e);
    }
  }

  private File createTestFile(TestFileResponse response) throws CoverError {
    try {
      File testFile = new File(response.path(), response.fileName());
      File parentDir = testFile.getParentFile();

      if (!parentDir.exists() && !parentDir.mkdirs()) {
        throw new CoverError("Failed to create directory structure for test file: " + testFile);
      }

      Files.writeString(testFile.toPath(), response.contents());
      return testFile;
    } catch (IOException e) {
      throw new CoverError("Failed to create test file: " + response.fileName(), e);
    }
  }


  private void testJavaCompileCommand() {
    JavaCompile javaCompileTask = project.getTasks().withType(JavaCompile.class).findByName("compileTestJava");
    StringBuilder builder = new StringBuilder();
    if (javaTestClassPath.isPresent()) {
      builder.append(JAVAC_COMMAND);
      for (String arg : javaCompileTask.getOptions().getAllCompilerArgs()) {
        builder.append(arg).append(" ");
      }
      builder.append("-d ").append(javaCompileTask.getDestinationDirectory().get().getAsFile().getAbsolutePath())
          .append(" ");
      builder.append("-classpath ");
      builder.append(javaTestClassPath.get());
      builder.append(" ");
      for (File sourceFile : javaCompileTask.getSource().getFiles()) {
        builder.append(sourceFile.getAbsolutePath()).append(" ");
      }
      String compileCommand = builder.toString();
      javaTestCompileCommand = Optional.ofNullable(compileCommand);
      logger.debug("Executing javac command: {}", compileCommand);
    } else {
      logger.error("No JavaCompile task found!");
    }
  }

  private void executeTestsWithCoverage(Map<File, String> testToSourceMap) {
    String jacocoReportPath = buildDirectory + "/jacocoTestReport.xml";
    String jacocoExecPath = buildDirectory + "/test.exec";
    for (Map.Entry<File, String> entry : testToSourceMap.entrySet()) {
      try {
        String javaAgentCommand = javaAgentCommand(jacocoExecPath);
        String jacocoJavaReportCommand = jacocoJavaReport(jacocoReportPath, jacocoExecPath);

        deleteFileIfExists(jacocoReportPath);
        deleteFileIfExists(jacocoExecPath);

        String testFile = entry.getKey().getAbsolutePath();
        String sourceFile = entry.getValue();

        String command =
            String.format("%s;%s;%s;%s", javaCompileCommand.get(), javaTestCompileCommand.get(), javaAgentCommand,
                jacocoJavaReportCommand);

        String success =
            coverageAiExecutor.execute(this.project, sourceFile, testFile, jacocoReportPath, command, projectPath);

        logger.debug("Success output from coverage-ai: {}", success);
      } catch (CoverError e) {
        logger.error("Failed to execute coverage for test file {} with source file {}", entry.getKey(),
            entry.getValue(), e);
      }
    }
  }

  private SourceMatchResult findMatchingSourceFiles(List<File> testFiles) {
    Map<File, String> testToSourceMap = new HashMap<>();
    List<File> remainingSourceFiles = new ArrayList<>(javaSourceFiles);

    for (File testFile : testFiles) {
      try {
        TestInfoResponse testInfoResponse = modelPrompter.chatter(remainingSourceFiles, testFile);
        String sourceFile = testInfoResponse.filepath();
        testToSourceMap.put(testFile, sourceFile);
        remainingSourceFiles.removeIf(file -> file.getAbsolutePath().equals(sourceFile));
        logger.info("Found matching source file {} for test file {}", sourceFile, testFile);
        logger.info("Remaining unmatched source files: {}", remainingSourceFiles.size());
      } catch (CoverError e) {
        logger.error("Failed to find a matching Source file from list {} for test file {}", javaSourceFiles, testFile,
            e);
      }
    }
    if (!remainingSourceFiles.isEmpty()) {
      logger.info("Unmatched source files remaining: {}", remainingSourceFiles);
    }
    return new SourceMatchResult(testToSourceMap, remainingSourceFiles);
  }


}
