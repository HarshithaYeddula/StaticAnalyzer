/*
 * The copyright of this file belongs to Koninklijke Philips N.V., 2019.
 */
package com.philips.bootcamp.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import com.philips.bootcamp.domain.Constants;
import jdk.internal.org.jline.utils.Log;

public class FileUtils {

  private FileUtils() {}
  public static String getFileContents(File file){
    if (file != null && file.exists() && Files.isExecutable(file.toPath())) {
      final StringBuilder fileContents = new StringBuilder();
      if (file.isDirectory()) {
        throw new IllegalArgumentException("[ERROR] Cannot read contents of file provided");
      }

      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

        String line = null;
        while ((line  = reader.readLine()) != null) {
          fileContents.append(line + "\n");
        }
      } catch (final IOException ioe) {
        Log.debug("[ERROR] Couldn't read from file");
        return null;
      }
      return fileContents.toString();
    }
    return null;
  }

  public static void writeFileContents(File file, String contents) {
    boolean result = false ;
    try {
      final String name = file.getName();
      if (!file.exists() && name != null && name.length() > 0) {
        result  = file.createNewFile();
      }
    } catch (final IOException ioe) {
      throw new IllegalArgumentException("[ERROR] Something went wrong during file creation!");
    }

    if(result) {
      if (file.isFile()) {
        if (contents == null) {
          contents = "";
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
          writer.write(contents);
        } catch (final IOException ioe) {
          Log.debug("[ERROR] Couldn't write to file");
        }
      } else {
        throw new IllegalArgumentException("[ERROR] Invalid file provided!");
      }
    }
  }

  public static void deleteDirectoryRecursion(Path path) throws IOException {
    if (path != null && path.toFile().exists()) {
      if((Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))) {
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
          for (final Path entry : entries) {
            deleteDirectoryRecursion(entry);
          }
        }
      }
      Files.delete(path);
    }
  }

  public static void deleteFolder(File directory) {
    if (directory == null || !directory.exists()) {
      return;
    }

    final StringBuilder command = new StringBuilder();
    command.append("\"" + new File(Constants.toolsDirectory, "rdir.bat").getAbsolutePath() + "\"");
    command.append(" \"" + directory.getAbsolutePath() + "\"");
    TerminalUtils.run(command.toString());
  }

  public static void renameFile(File file, String newName) {
    final StringBuilder command = new StringBuilder();
    command.append("\"" + new File(Constants.toolsDirectory, "rename.bat").getAbsolutePath() + "\"");
    command.append(" \"" + file.getAbsolutePath() + "\"");
    command.append(" \"" + newName + "\"");
    TerminalUtils.run(command.toString());
  }
}