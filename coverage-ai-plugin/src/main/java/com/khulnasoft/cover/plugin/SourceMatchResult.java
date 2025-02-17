package com.khulnasoft.cover.plugin;

import java.io.File;
import java.util.List;
import java.util.Map;

public record SourceMatchResult(Map<File, String> testToSourceMap, List<File> remainingSourceFiles) {
}
