package com.philips.bootcamp.domain;

import com.google.gson.JsonObject;

public interface Tool {
    String getName();
    String getDescription();
    JsonObject execute(JsonObject settings);
    JsonObject compare(JsonObject futureReport, JsonObject pastReport);
    boolean verifySettings(JsonObject settings);
    JsonObject getDefaultSettings();
}