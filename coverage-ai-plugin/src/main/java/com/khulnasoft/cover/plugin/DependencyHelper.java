package com.khulnasoft.cover.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.logging.Logger;

public class DependencyHelper {

  private Project project;
  private Logger logger;

  public DependencyHelper(Project project, Logger logger) {
    this.project = project;
    this.logger = logger;
  }


  public List<String> findNeededJars(String mavenDependencyPath) throws CoverError {
    List<String> jarPaths = new ArrayList<>();
    try {
      ConfigurationContainer c = project.getConfigurations();
      DependencyHandler handler = project.getDependencies();
      Dependency dependency = handler.create(mavenDependencyPath);
      Configuration configuration = c.detachedConfiguration(dependency);
      Set<File> files = configuration.resolve();
      jarPaths.addAll(files.stream().map(File::getAbsolutePath).collect(Collectors.toList()));
      for (String jarPath : jarPaths) {
        logger.debug("Found jar path {}", jarPath);
      }
    } catch (Exception e) {
      logger.error("Failed to find needed jars {}", mavenDependencyPath, e);
      throw new CoverError("Failed to find " + mavenDependencyPath, e);
    }
    return jarPaths;
  }
}
