package com.khulnasoft.cover.plugin;

public record TestFileRequest(String sourceFilePath, String sourceContent, String testingFramework) {
}
