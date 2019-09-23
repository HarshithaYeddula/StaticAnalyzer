/*
 * The copyright of this file belongs to Koninklijke Philips N.V., 2019.
 */
package com.philips.bootcamp.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GenericUtils {

  private GenericUtils() {

  }

  public static <T> List<T> castList(Class<? extends T> clazz, Collection<?> c) {
    if (clazz == null || c == null) {
      throw new IllegalArgumentException("[ERROR] Null argument(s)");
    }

    final List<T> r = new ArrayList<>(c.size());
    for(final Object o: c) {
      r.add(clazz.cast(o));
    }
    return r;
  }
}