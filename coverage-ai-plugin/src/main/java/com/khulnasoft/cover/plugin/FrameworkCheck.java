package com.khulnasoft.cover.plugin;

import org.gradle.api.artifacts.Dependency;

public class FrameworkCheck {
  private final String identifier;
  private final boolean checkVersion;
  private final String version;

  FrameworkCheck(String identifier) {
    this(identifier, false, "-100");
  }


  FrameworkCheck(String identifier, String version) {
    this(identifier, true, version);
  }

  FrameworkCheck(String identifier, boolean checkVersion, String version) {
    this.identifier = identifier;
    this.checkVersion = checkVersion;
    this.version = version;
  }

  boolean matches(Dependency dependency) {
    if (checkVersion) {
      return dependency.getVersion() != null && dependency.getVersion().startsWith(version);
    }
    return dependency.getGroup() != null && dependency.getGroup().contains(identifier);
  }

  String getFramework() {
    return identifier;
  }

}
