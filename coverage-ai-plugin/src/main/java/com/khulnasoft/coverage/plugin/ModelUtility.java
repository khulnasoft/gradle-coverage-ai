package com.khulnasoft.coverage.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.gradle.api.logging.Logger;

public class ModelUtility {
  private Logger logger;

  public ModelUtility(Logger logger) {
    this.logger = logger;
  }

  public String extractJson(String text) {
    if (text != null) {
      return text.substring(text.indexOf('{'), text.lastIndexOf('}') + 1);
    } else {
      return "{}";
    }
  }

  public String readFile(File file) {
    String contents = "";
    try {
      contents = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
    } catch (IOException e) {
      logger.error("Error with file {} check your project you will not have accurate outcomes", file, e);
    }
    return contents;
  }
}
