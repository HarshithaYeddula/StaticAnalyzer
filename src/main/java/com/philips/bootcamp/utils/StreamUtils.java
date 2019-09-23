/*
 * The copyright of this file belongs to Koninklijke Philips N.V., 2019.
 */
package com.philips.bootcamp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtils {

  private StreamUtils() {

  }

  public static String getStreamContents(InputStream is) {
    if (is != null) {
      final StringBuilder fileContents = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

        String line = null;
        while ((line  = reader.readLine()) != null) {
          fileContents.append(line + "\n");
        }
      } catch (final IOException ioe) {
        return null;
      }
      final int length = fileContents.length();
      fileContents.delete(length-1, length);
      return fileContents.toString();
    }

    return null;
  }
}