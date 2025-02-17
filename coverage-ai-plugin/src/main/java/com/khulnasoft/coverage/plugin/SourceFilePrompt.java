package com.khulnasoft.coverage.plugin;

import java.util.List;

public record SourceFilePrompt(List<String> sourceFiles, String fileName, String content) {
}
