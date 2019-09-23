/*
 * The copyright of this file belongs to Koninklijke Philips N.V., 2019.
 */
package com.philips.bootcamp.domain;

import java.io.File;

public class Constants {
  public static final File rootDirectory;
  public static final File toolsDirectory;
  public static final File sourceDirectory;
  public static final File dataDirectory;
  public static final File sampleDirectory;
  public static final File output;

  private Constants() {}

  static {
    String rootPath = System.getenv("FENCES_HOME");

    if (System.getenv(rootPath) == null) {
      rootDirectory = new File(rootPath);
    } else {
      rootPath = "C:\\";
      rootDirectory = new File(rootPath);
    }

    toolsDirectory = new File(rootDirectory, "tools");
    if (!toolsDirectory.exists()) {
      toolsDirectory.mkdir();
    }

    sourceDirectory = new File(rootDirectory, "source");
    if (!sourceDirectory.exists()) {
      sourceDirectory.mkdir();
    }

    dataDirectory = new File(rootDirectory, "data");
    if (!dataDirectory.exists()) {
      dataDirectory.mkdir();
    }

    sampleDirectory = new File(rootDirectory, "sample");
    if (!sampleDirectory.exists()) {
      sampleDirectory.mkdir();
    }

    output = new File(rootDirectory, "out");
  }
}