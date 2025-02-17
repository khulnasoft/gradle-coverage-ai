package com.khulnasoft.cover.plugin;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class CoverageAiExecutor {
    public static final String WANDB_API_KEY = "WANDB_API_KEY";
    public static final String OPENAI_API_KEY = "OPENAI_API_KEY";
    private final String coverageAiBinaryPath;
    private final String wanDBApiKey;
    private final String apiKey;
    private final int coverage;
    private final int iterations;

    private CoverageAiExecutor(Builder builder) {
        this.coverageAiBinaryPath = builder.coverageAiBinaryPath;
        this.wanDBApiKey = builder.wanDBApiKey;
        this.apiKey = builder.apiKey;
        this.coverage = builder.coverage;
        this.iterations = builder.iterations;
    }

    public String execute(Project project, String sourceFile, String testFile, String jacocoReportPath,
                          String commandString, String projectPath) throws CoverError {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        ExecResult result = project.exec(getExecSpecAction(sourceFile, testFile, jacocoReportPath,
                commandString, projectPath));

        if (result.getExitValue() != 0) {
            String errorOutput = errorStream.toString(StandardCharsets.UTF_8);
            throw new CoverError("An error occurred while executing coverage agent " + result + "\n" + errorOutput);
        }
        String output = "Invalid encoding";
        output = outputStream.toString(StandardCharsets.UTF_8);
        return output;
    }

    private Action<ExecSpec> getExecSpecAction(String sourceFile, String testFile, String jacocoReportPath,
                                               String commandString, String projectPath) {
        return (ExecSpec execSpec) -> {
            if (wanDBApiKey != null && !wanDBApiKey.isEmpty()) {
                execSpec.environment(WANDB_API_KEY, wanDBApiKey);
            } else {
                execSpec.environment(OPENAI_API_KEY, apiKey);
            }
            execSpec.commandLine(coverageAiBinaryPath, "--source-file-path=" + sourceFile, "--test-file-path="
                            + testFile, "--code-coverage-report-path=" + jacocoReportPath, "--test-command="
                            + commandString, "--test-command-dir=" + projectPath, "--coverage-type=jacoco",
                    "--desired-coverage="
                            + coverage, "--max-iterations=" + iterations);
            execSpec.setWorkingDir(projectPath);
        };
    }

    public static class Builder {
        private String coverageAiBinaryPath;
        private String wanDBApiKey;
        private String apiKey;
        private int coverage;
        private int iterations;

        public Builder coverageAiBinaryPath(String coverageAiBinaryPath) {
            this.coverageAiBinaryPath = coverageAiBinaryPath;
            return this;
        }

        public Builder coverage(int coverage) {
            this.coverage = coverage;
            return this;
        }

        public Builder iterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public Builder wanDBApiKey(String wanDBApiKey) {
            this.wanDBApiKey = wanDBApiKey;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public CoverageAiExecutor build() {
            return new CoverageAiExecutor(this);
        }
    }
}
