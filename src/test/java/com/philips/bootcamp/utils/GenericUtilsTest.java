package com.philips.bootcamp.utils;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class GenericUtilsTest {

    @Test(expected = RuntimeException.class)
    public void castListNullArgs() {
        GenericUtils.castList(null, null);
    }

    @Test(expected = RuntimeException.class)
    public void classArgIsNull() {
        GenericUtils.castList(null, new ArrayList<String>());
    }

    @Test(expected = RuntimeException.class)
    public void collectionArgIsNull() {
        GenericUtils.castList(GenericUtils.class, null);
    }

    @Test
    public void properArgs() {
        List<Object> objects = List.of("1", "2", "3");
        List<String> strings = GenericUtils.castList(String.class, objects);
        assertTrue(strings.containsAll(objects));
    }

    @Test(expected = ClassCastException.class)
    public void improperArgs() {
        List<Object> objects = List.of("1", "2", "3");
        GenericUtils.castList(Integer.class, objects);
    }
}