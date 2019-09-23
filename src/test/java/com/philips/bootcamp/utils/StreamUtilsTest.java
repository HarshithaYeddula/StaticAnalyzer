package com.philips.bootcamp.utils;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;


public class StreamUtilsTest {
    @Test
    public void nullStream() {
        String content = StreamUtils.getStreamContents(null);
        assertEquals(null, content);
    }

    @Test
    public void emptyStream() {
        String content = StreamUtils.getStreamContents(new ByteArrayInputStream(" ".getBytes()));
        assertEquals(" ", content);
    }

    @Test
    public void properStream() {
        String content = "some content..kvkwnv";
        String actual = StreamUtils.getStreamContents(new ByteArrayInputStream(content.getBytes()));
        assertEquals(content, actual);
    }
}