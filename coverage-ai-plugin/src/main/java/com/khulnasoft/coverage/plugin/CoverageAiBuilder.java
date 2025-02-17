package com.khulnasoft.coverage.plugin;

import dev.langchain4j.model.openai.OpenAiChatModel.OpenAiChatModelBuilder;
import org.gradle.api.Project;

import java.util.Optional;

public class CoverageAiBuilder {
    private String apiKey;
    private String wandbApiKey;
    private int iterations;
    private int coverage;
    private String coverageAiBinaryPath;
    private ModelPrompter modelPrompter;
    private String javaClassPath;
    private String javaTestClassPath;
    private String projectPath;
    private String javaClassDir;
    private String buildDirectory;
    private CoverageAiExecutor coverageAiExecutor;
    private Project project;
    private OpenAiChatModelBuilder openAiChatModelBuilder;

    public static CoverageAiBuilder builder() {
        return new CoverageAiBuilder();
    }

    public CoverageAiBuilder apiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public CoverageAiBuilder wandbApiKey(String wandbApiKey) {
        this.wandbApiKey = wandbApiKey;
        return this;
    }

    public CoverageAiBuilder iterations(int iterations) {
        this.iterations = iterations;
        return this;
    }

    public CoverageAiBuilder coverage(int coverage) {
        this.coverage = coverage;
        return this;
    }

    public CoverageAiBuilder coverageAiBinaryPath(String coverageAiBinaryPath) {
        this.coverageAiBinaryPath = coverageAiBinaryPath;
        return this;
    }

    public CoverageAiBuilder modelPrompter(ModelPrompter modelPrompter) {
        this.modelPrompter = modelPrompter;
        return this;
    }

    public CoverageAiBuilder javaClassPath(String javaClassPath) {
        this.javaClassPath = javaClassPath;
        return this;
    }

    public CoverageAiBuilder javaTestClassPath(String javaTestClassPath) {
        this.javaTestClassPath = javaTestClassPath;
        return this;
    }

    public CoverageAiBuilder projectPath(String projectPath) {
        this.projectPath = projectPath;
        return this;
    }

    public CoverageAiBuilder javaClassDir(String javaClassDir) {
        this.javaClassDir = javaClassDir;
        return this;
    }

    public CoverageAiBuilder buildDirectory(String buildDirectory) {
        this.buildDirectory = buildDirectory;
        return this;
    }

    public CoverageAiBuilder coverageAiExecutor(CoverageAiExecutor coverageAiExecutor) {
        this.coverageAiExecutor = coverageAiExecutor;
        return this;
    }

    public CoverageAiBuilder project(Project project) {
        this.project = project;
        return this;
    }

    public CoverageAiBuilder openAiChatModelBuilder(OpenAiChatModel.OpenAiChatModelBuilder openAiChatModelBuilder) {
        this.openAiChatModelBuilder = openAiChatModelBuilder;
        return this;
    }


    /**
     * Builds and returns a new instance of CoverageAi using the current state of the builder.
     *
     * @return a new CoverageAi instance
    public OpenAiChatModel.OpenAiChatModelBuilder getOpenAiChatModelBuilder() {
        return openAiChatModelBuilder;
    }
    }
    /**
     * Returns the API key.
     * @return the API key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Returns the Weights & Biases API key.
     * @return the Weights & Biases API key
     */
    public String getWandbApiKey() {
        return wandbApiKey;
    }

    /**
     * Returns the number of iterations.
     * @return the number of iterations
     */
    public int getIterations() {
        return iterations;
    }

    /**
     * Returns the coverage percentage.
     * @return the coverage percentage
     */
    public int getCoverage() {
        return coverage;
    }

    /**
     * Returns the path to the Coverage AI binary.
     * @return the path to the Coverage AI binary
     */
    public String getCoverageAiBinaryPath() {
        return coverageAiBinaryPath;
    }

    /**
     * Returns the model prompter.
     * @return the model prompter
     */
    public ModelPrompter getModelPrompter() {
        return modelPrompter;
    }

    /**
     * Returns the Java class path.
     * @return the Java class path
     */
    public Optional<String> getJavaClassPath() {
        return javaClassPath;
    }

    /**
     * Returns the Java test class path.
     * @return the Java test class path
     */
    public Optional<String> getJavaTestClassPath() {
        return javaTestClassPath;
    }

    /**
     * Returns the project path.
     * @return the project path
     */
    public String getProjectPath() {
        return projectPath;
    }

    /**
     * Returns the Java class directory.
     * @return the Java class directory
     */
    public Optional<String> getJavaClassDir() {
        return javaClassDir;
    }

    /**
     * Returns the build directory.
     * @return the build directory
     */
    public String getBuildDirectory() {
        return buildDirectory;
    }

    /**
     * Returns the Coverage AI executor.
     * @return the Coverage AI executor
     */
    public CoverageAiExecutor getCoverageAiExecutor() {
        return coverageAiExecutor;
    }

    /**
     * Returns the Gradle project.
     * @return the Gradle project
     */
    public Project getProject() {
        return project;
    }
    }

    public Project getProject() {
        return project;
    }
}
