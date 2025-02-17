package com.khulnasoft.coverage.plugin;

public record TestFileRequest(String sourceFilePath, String sourceContent, String testingFramework) {
}
