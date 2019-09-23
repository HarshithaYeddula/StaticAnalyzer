package com.philips.bootcamp.utils;

public class TerminalUtils {

    private TerminalUtils() {}

    public static String run(String command) {
        String output = null;

        try {
            Process p = Runtime.getRuntime().exec(command);
            output = StreamUtils.getStreamContents(p.getInputStream());
        } catch (Exception exception) {
            return null;
        }

        return output;
    }
}