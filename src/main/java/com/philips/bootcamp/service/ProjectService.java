/*
 * The copyright of this file belongs to Koninklijke Philips N.V., 2019.
 */
package com.philips.bootcamp.service;

import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.philips.bootcamp.domain.Project;
import com.philips.bootcamp.tools.ToolName;

public interface ProjectService {
  String fenceProject(String name) throws Throwable;
  List<Project> findAll();
  Project find(String name);
  String save(String project);
  boolean delete(String name);

  String updateSettings(String name, String settings);
  String getReport(String name);
  String getSettings(String name);

  String getInstantReport(String toolname, String source);

  default String getTools() {
    final JsonParser parser = new JsonParser();
    final JsonObject tools = new JsonObject();
    for (final ToolName tool : ToolName.values()) {
      JsonObject toolObject = null;
      try {
        toolObject = parser.parse(tool.getInstance().getDescription()).getAsJsonObject();
      } catch (final JsonParseException jpe) {

      }
      tools.add(tool.toString(), toolObject);
    }
    return tools.toString();
  }
}