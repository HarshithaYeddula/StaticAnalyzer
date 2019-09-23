/*
 * The copyright of this file belongs to Koninklijke Philips N.V., 2019.
 */
package com.philips.bootcamp.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class StringUtils {
  private StringUtils() {
  }

  public static String getProjectNameFromHttpLink(String link) {
    if (link == null) {
      return null;
    }

    String projectName = null;
    try {
      new URL(link);
      final int indexOfLastForwardSlash = link.lastIndexOf('/');
      final int indexOfLastPeriod = link.lastIndexOf('.');
      if (indexOfLastForwardSlash != -1 && indexOfLastPeriod != -1) {
        projectName = link.substring(indexOfLastForwardSlash + 1, indexOfLastPeriod);
      }
    } catch (final MalformedURLException e) {
      return null;
    }

    return projectName;

  }
}