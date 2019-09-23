/*
 * The copyright of this file belongs to Koninklijke Philips N.V., 2019.
 */
package com.philips.bootcamp.service;

import java.io.File;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.philips.bootcamp.dal.ProjectDAO;
import com.philips.bootcamp.domain.Constants;
import com.philips.bootcamp.domain.Project;
import com.philips.bootcamp.domain.Tool;
import com.philips.bootcamp.tools.ToolName;
import com.philips.bootcamp.utils.FileUtils;
import com.philips.bootcamp.utils.StringUtils;
import com.philips.bootcamp.utils.TerminalUtils;

@Service
public class ProjectServiceImpl implements ProjectService {

  public static final String SETTINGSFILE = "settings.json";
  public static final String REPORTFILE = "report.json";

  @Autowired
  ProjectDAO projectDAO;

  File parent = Constants.sourceDirectory;

  public void setProjectDAO(ProjectDAO projectDAO) {
    this.projectDAO = projectDAO;
  }

  public void setParentFile(File parent) {
    this.parent = parent;
  }

  @Override
  public List<Project> findAll() {
    return projectDAO.findAll();
  }

  @Override
  public Project find(String name) {
    return projectDAO.find(name);
  }

  @Override
  public String save(String project) {
    final String  message1="Invalid project json";
    final String Settings= "settings";

    if (project == null) {
      return message1;
    }

    final JsonParser parser = new JsonParser();
    JsonObject projectJsonObject = new JsonObject();

    try {
      projectJsonObject = parser.parse(project).getAsJsonObject();
    } catch (JsonParseException|IllegalStateException exception) {
      return message1;
    }

    if (projectJsonObject == null) {
      return message1;
    }

    if (!projectJsonObject.has("link")) {
      return "Project json must have property 'link'";
    }
    if (!projectJsonObject.has("branch")) {
      return "Project json must have property 'branch'";
    }

    final String projectLink = projectJsonObject.get("link").getAsString();
    final String projectName = StringUtils.getProjectNameFromHttpLink(projectLink);
    if (projectName == null) {
      return "Illegal property value for 'link'";
    }

    final String projectBranch = projectJsonObject.get("branch").getAsString();
    if (projectBranch == null) {
      return "Illegal property value for 'branch'";
    }

    final Project projectObject = new Project();
    final Project existing = find(projectName);
    if (existing == null) {
      projectObject.setName(projectName + "-" + projectBranch);
      projectObject.setProjectCreationDate(new Date(System.currentTimeMillis()));
      projectDAO.save(projectObject);
    }

    File projectFolder = new File(parent, projectName + "-" + projectBranch);

    checkExistence(projectFolder);

    projectFolder = new File(parent, projectName);

    final String cloneResult = cloneGitProject(projectLink);
    if (cloneResult != null) {
      return cloneResult;
    }

    FileUtils.renameFile(projectFolder, projectName + "-" + projectBranch);
    switchBranch(projectName + "-" + projectBranch, projectBranch);

    checkSettings(projectJsonObject,Settings,projectName,projectBranch);


    return (existing == null)? "Project created" : "Project updated";
  }


  private void checkSettings(JsonObject projectJsonObject, String settings, String projectName, String projectBranch) {
    final JsonParser parser = new JsonParser();
    if (projectJsonObject.has(settings)) {
      final Set<String> tools = projectJsonObject.get(settings).getAsJsonObject().keySet();
      final JsonObject effectiveSettings = new JsonObject();

      for (final String tool : tools) {
        final JsonObject object = new JsonObject();
        final ToolName toolname = getTool(tool);
        if (toolname == null) {
          continue;
        }

        JsonObject toolParams;
        try {
          toolParams = parser.parse(toolname.getInstance().getDescription()).getAsJsonObject().get("parameters").getAsJsonObject();
        } catch (final JsonParseException jpe) { continue; }

        final Set<String> params = toolParams.keySet();
        for (final String param : params) {
          object.addProperty(param, toolParams.get(param).getAsJsonObject().get("default").getAsString());
        }

        final JsonObject userSettings = projectJsonObject.get(settings).getAsJsonObject().get(tool).getAsJsonObject();
        final Set<String> customSettings = userSettings.keySet();
        for (final String customSetting : customSettings) {
          object.addProperty(customSetting, userSettings.get(customSetting).getAsString());
        }
        effectiveSettings.add(tool, object);
      }

      final File settingsFolder = new File(Constants.dataDirectory, projectName + "-" + projectBranch);
      settingsFolder.mkdir();

      final File settingsJson = new File(settingsFolder, SETTINGSFILE);
      FileUtils.writeFileContents(settingsJson, effectiveSettings.toString());
    }

  }

  public  void checkExistence(File projectFolder) {
    if (projectFolder.exists()) {
      FileUtils.deleteFolder(projectFolder);
    }

  }

  private String cloneGitProject(String link) {
    final StringBuilder command = new StringBuilder();
    command.append("\"" + new File(Constants.toolsDirectory, "action.bat").getAbsolutePath() + "\"");
    command.append(" \"" + Constants.sourceDirectory.getAbsolutePath() + "\"");
    command.append(" \"git clone " + link + "\"");
    final String output = TerminalUtils.run(command.toString());
    if (output == null) {
      return null;
    }
    return output;
  }

  private void switchBranch(String project, String branch) {
    final StringBuilder command = new StringBuilder();
    command.append("\"" + new File(Constants.toolsDirectory, "switch.bat").getAbsolutePath() + "\"");
    command.append(" \"" + new File(Constants.sourceDirectory, project).getAbsolutePath() + "\"");
    command.append(" " + branch);
    TerminalUtils.run(command.toString());
  }

  private ToolName getTool(String toolname) {
    final String s= toolname.toLowerCase();
    switch(s) {
      case "checkstyle": return ToolName.CHECKSTYLE;
      case "pmd": return ToolName.PMD;
      case "maven": return ToolName.MAVEN;
      default: return null;

    }
  }




  @Override
  public boolean delete(String name) {
    final Project existing = find(name);
    if (existing == null) {
      return false;
    }

    File folder = new File(Constants.sourceDirectory, name);
    FileUtils.deleteFolder(folder);

    folder = new File(Constants.dataDirectory, name);
    FileUtils.deleteFolder(folder);

    projectDAO.delete(name);
    return true;
  }

  @Override
  public String fenceProject(String name){
    JsonObject output = null;
    final Project project = find(name);
    if (project == null) {
      return "{\"status\":\"fail\",\"error\" : \"No project found with the name: " + name + "\"}";
    }

    final JsonParser parser = new JsonParser();
    JsonObject projectSettings;

    final File projectFolder = new File(Constants.sourceDirectory, name);
    final File projectDataDirectory = new File(Constants.dataDirectory, name);

    try {
      final String fileContent = FileUtils.getFileContents(new File(projectDataDirectory, SETTINGSFILE));
      projectSettings = parser.parse(fileContent).getAsJsonObject();
    } catch (final Exception jpe) {
      return "{\"status\":\"fail\",\"error\" : \"Exception encountered while reading project settings\"}";
    }

    JsonObject prevReport;
    try {
      final String fileContent = FileUtils.getFileContents(new File(projectDataDirectory, REPORTFILE));
      prevReport = parser.parse(fileContent).getAsJsonObject();
    } catch (final Exception e) {
      prevReport = new JsonObject();
    }

    final JsonObject comparisons = new JsonObject();
    final JsonObject report = new JsonObject();

    final Set<String> tools = projectSettings.keySet();
    for (final String tool : tools) {
      final JsonObject toolSettings = projectSettings.get(tool).getAsJsonObject();
      toolSettings.addProperty("project", projectFolder.getAbsolutePath());

      output = getTool(tool).getInstance().execute(toolSettings);
      report.add(tool, output);

      final JsonElement prevToolReport = prevReport.get(tool);

      if (prevToolReport != null) {

        final JsonObject comparison=getTool(tool).getInstance().compare(output, prevToolReport.getAsJsonObject());
        comparisons.add(tool, comparison);

      } else {

        final JsonObject comparison=getTool(tool).getInstance().compare(output, null);
        comparisons.add(tool, comparison);
      }



    }


    FileUtils.writeFileContents(new File(projectDataDirectory, REPORTFILE), report.toString());
    project.setLastBuildDate(new Date(System.currentTimeMillis()));
    projectDAO.update(project);

    return "{\"status\":\"pass\",\"report\" : " + comparisons.toString() + "}";
  }

  @Override
  public String updateSettings(String name, String settings) {
    if (name == null) {
      return "Project name required";
    }
    if (settings == null) {
      return "No settings provided";
    }

    final Project project = find(name);
    if (project == null) {
      return "No project found with name: " + name;
    }


    final JsonParser parser = new JsonParser();
    JsonObject projectSettings = null;
    JsonObject actualProjectSettings = null;

    final File projectDataDirectory = new File(Constants.dataDirectory, name);
    try {
      projectSettings = parser.parse(settings).getAsJsonObject();
    } catch(final Exception exception) {
      return "Invalid settings json string";
    }

    try {
      actualProjectSettings = parser.parse(FileUtils.getFileContents(new File(projectDataDirectory, SETTINGSFILE))).getAsJsonObject();
    } catch(final Exception exception) {
      actualProjectSettings = new JsonObject();
    }

    final Set<String> tools = projectSettings.keySet();

    for (final String tool : tools) {
      final ToolName iTool = getTool(tool);
      if (iTool == null) {
        continue;
      }

      final Tool toolInstance = iTool.getInstance();
      final JsonObject toolSettings = projectSettings.get(tool).getAsJsonObject();
      if (!toolInstance.verifySettings(toolSettings)) {
        continue;
      }

      final JsonElement actualToolElement = actualProjectSettings.get(tool);
      if (actualToolElement == null) {
        actualProjectSettings.add(tool, toolInstance.getDefaultSettings());
      }

      final JsonObject actualToolSettings = actualProjectSettings.get(tool).getAsJsonObject();
      updateToolSettings(actualToolSettings, toolSettings);
    }

    final Set<String> rTools = setDifference(actualProjectSettings.keySet(), tools);
    for (final String tool : rTools) {
      actualProjectSettings.remove(tool);
    }

    FileUtils.writeFileContents(new File(projectDataDirectory, SETTINGSFILE), actualProjectSettings.toString());
    return "Project settings updated";
  }

  private Set<String> setDifference(Set<String> setA, Set<String> setB) {
    final Set<String> difference = new HashSet<>();
    for (final String string : setA) {
      if (!setB.contains(string)) {
        difference.add(string);
      }
    }
    return difference;
  }

  private void updateToolSettings(JsonObject actualToolSettings, JsonObject toolSettings) {
    final Set<String> parameters = toolSettings.keySet();

    for (final String param : parameters) {
      actualToolSettings.addProperty(param, toolSettings.get(param).getAsString());
    }
  }

  @Override
  public String getReport(String name) {
    return getFile(name, REPORTFILE);
  }

  @Override
  public String getSettings(String name) {
    return getFile(name, SETTINGSFILE);
  }

  private String getFile(String projectName, String fileName) {
    if (projectName == null) {
      return "Project name required";
    }

    final Project project = find(projectName);
    if (project == null) {
      return "No project by name: " + projectName;
    }

    final File file = new File(Constants.dataDirectory, projectName + "/" + fileName);
    if (!file.exists()) {
      return "File not present";
    }

    return FileUtils.getFileContents(file);
  }

  @Override
  public String getInstantReport(String toolname, String source) {
    if (toolname == null) {
      return "{\"error\" : \"Invalid toolname\"}";
    }
    if (source == null) {
      return "{\"error\" : \"No content provided\"}";
    }

    final ToolName toolName = getTool(toolname);
    if (toolName == null) {
      return "{\"error\" : \"No such tool\"}";
    }

    final File testFile = new File(Constants.sampleDirectory, "Test.java");
    FileUtils.writeFileContents(testFile, source);
    final Tool tool = toolName.getInstance();
    final JsonObject defaultSettings = tool.getDefaultSettings();
    defaultSettings.addProperty("project", Constants.sampleDirectory.getAbsolutePath());
    final JsonObject report = tool.execute(defaultSettings);
    report.addProperty("error", "none");
    return report.toString();
  }
}