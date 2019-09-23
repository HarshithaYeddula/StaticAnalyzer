/*
 * The copyright of this file belongs to Koninklijke Philips N.V., 2019.
 */
package com.philips.bootcamp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import com.google.gson.JsonObject;
import com.philips.bootcamp.dal.ProjectDAO;
import com.philips.bootcamp.domain.Project;
import com.philips.bootcamp.tools.Checkstyle;
import com.philips.bootcamp.utils.FileUtils;

public class ProjectServiceImplTest {

  @Test
  public void findAll() {
    final ProjectDAO dao = Mockito.mock(ProjectDAO.class);
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final List<Project> projects = List.of(new Project());
    Mockito.when(dao.findAll()).thenReturn(projects);
    psi.setProjectDAO(dao);
    assertEquals(projects, psi.findAll());
  }

  @Test
  public void find() {
    final ProjectDAO dao = Mockito.mock(ProjectDAO.class);
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final Project project = new Project();
    Mockito.when(dao.find("name")).thenReturn(project);
    psi.setProjectDAO(dao);
    assertEquals(project, psi.find("name"));
  }

  @Test
  public void saveWhenWithProjectStringAsRandomText() {
    final String randomtext = "isdbbskdc";
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final String result = psi.save(randomtext);
    assertEquals("Invalid project json", result);
  }

  @Test
  public void saveWithProjectStringIsNull() {
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final String result = psi.save(null);
    assertEquals("Invalid project json", result);
  }

  @Test
  public void saveWithProjectStringAsEmpty() {
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final String result = psi.save("");
    assertEquals("Invalid project json", result);
  }

  @Test
  public void saveButProjectFolderDoesNotExist() {
    final File parent = createDirectory("source");

    final ProjectServiceImpl psi = new ProjectServiceImpl();
    psi.setParentFile(parent);
    final String result = psi.save("{\"link\":\"http://google.com/Some.git\",\"branch\":\"master\", \"settings\":{}}");

    assertEquals("Project created",result);
    deleteDirectory(parent);
  }

  @Test
  public void saveButProjectFolderIsNotFolder() throws IOException {
    final File parent = createDirectory("source");
    final File test = new File(parent, "test");
    test.createNewFile();

    final ProjectServiceImpl psi = new ProjectServiceImpl();
    psi.setParentFile(parent);
    final String result = psi.save("{\"link\":\"http://google.com/Some.git\",\"branch\":\"master\", \"settings\":{}}");

    assertEquals("Project created",result);
    deleteDirectory(parent);
  }

  @Test
  public void saveButProjectAlreadyExists() throws IOException {
    final File parent = createDirectory("source");
    final File test = new File(parent, "test");
    test.mkdir();

    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final ProjectDAO dao = Mockito.mock(ProjectDAO.class);
    Mockito.when(dao.find("test")).thenReturn(new Project());
    psi.setProjectDAO(dao);
    psi.setParentFile(parent);

    final String result = psi.save("{\"link\":\"http://google.com/Some.git\",\"branch\":\"master\", \"settings\":{}}");

    assertEquals("Project created", result);

    deleteDirectory(parent);
  }

  @Test
  public void saveButProjectHasNoSettings() throws IOException {
    final File parent = createDirectory("source");
    final File test = new File(parent, "test");
    test.mkdir();
    final Project p = new Project();

    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final ProjectDAO dao = Mockito.mock(ProjectDAO.class);
    Mockito.when(dao.find("test")).thenReturn(p);
    Mockito.when(dao.save(Mockito.any(Project.class))).thenReturn(null);
    psi.setProjectDAO(dao);
    psi.setParentFile(parent);

    final String result = psi.save("{\"link\":\"http://google.com/Some.git\",\"branch\":\"master\"}");

    assertEquals("Project created", result);

    deleteDirectory(parent);
  }

  @Test
  public void saveWithProjectSettings() throws IOException {
    final File parent = createDirectory("source");
    final File test = new File(parent, "test");
    test.mkdir();

    final Project p = new Project();
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final ProjectDAO dao = Mockito.mock(ProjectDAO.class);
    Mockito.when(dao.find("test")).thenReturn(p);
    Mockito.when(dao.save(Mockito.any(Project.class))).thenReturn(null);
    psi.setProjectDAO(dao);
    psi.setParentFile(parent);
    final String result = psi.save("{\"link\":\"http://google.com/Some.git\",\"branch\":\"master\", \"settings\":{}}");

    assertEquals("Project created", result);
    deleteDirectory(parent);
  }



  @Test
  public void getReport_When_Project_Name_Is_Null() {
    final ProjectServiceImpl psi = new ProjectServiceImpl();

    final String result = psi.getReport(null);
    assertEquals("Project name required", result);
  }

  @Test
  public void getReport_When_Project_Doesnt_exist() {
    final ProjectServiceImpl psi = Mockito.mock(ProjectServiceImpl.class);
    Mockito.when(psi.find("project")).thenReturn(null);
    Mockito.when(psi.getReport("project")).thenCallRealMethod();
    assertEquals("No project by name: project",psi.getReport("project"));
  }

  @Test
  public void getReportShouldReturnReportWhenProjectReportExists() {
    final File parent = createDirectory("source");
    final File project = new File(parent, "project");
    project.mkdir();
    final File report = new File(project, "report.json");
    FileUtils.writeFileContents(report, "contents");
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final ProjectDAO dao = Mockito.mock(ProjectDAO.class);
    final Project p = new Project();
    Mockito.when(dao.find("project")).thenReturn(p);
    psi.setParentFile(parent);
    psi.setProjectDAO(dao);
    final String out = psi.getReport("project");
    assertEquals("contents\n", out);
    deleteDirectory(parent);
  }

  @Test
  public void getReportShouldReturnNullWhenProjectReportDoesntExists() {
    final File parent = createDirectory("source");
    final File project = new File(parent, "project");
    project.mkdir();
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final ProjectDAO dao = Mockito.mock(ProjectDAO.class);
    Mockito.when(dao.find("project")).thenReturn(new Project());
    psi.setParentFile(parent);
    psi.setProjectDAO(dao);
    final String out = psi.getReport("project");
    assertNull(out);
    deleteDirectory(parent);
  }

  @Test
  public void getSettingsShouldReturnNullWhenProjectNameIsNull() {
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    assertNull(psi.getSettings(null));
  }

  @Test
  public void returnFalseWhenDeletingAProjectThatDoesnNotExist() {
    final ProjectServiceImpl psi = Mockito.mock(ProjectServiceImpl.class);
    Mockito.when(psi.find("null")).thenReturn(null);
    Mockito.when(psi.delete("null")).thenCallRealMethod();
    assertFalse(psi.delete("null"));
  }

  @Test
  public void returnTrueWhenDeletingAProject() {
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final ProjectDAO dao = Mockito.mock(ProjectDAO.class);
    Mockito.when(dao.find("project")).thenReturn(new Project());
    final File parent = createDirectory("source");
    final File project = new File(parent, "project");
    project.mkdir();
    psi.setProjectDAO(dao);
    psi.setParentFile(parent);
    assertTrue(psi.delete("project"));
    assertFalse(project.exists());
    deleteDirectory(parent);
  }

  @Test
  public void buildFailsWhenProjectDoesNotExist() {
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final ProjectDAO dao = Mockito.mock(ProjectDAO.class);
    Mockito.when(dao.find("project")).thenReturn(null);
    psi.setProjectDAO(dao);
    assertEquals("{\"error\" : \"No project found with the name: project\"}", psi.buildProject("project"));
  }

  @Test
  public void buildFailsWhenSettingsFileNotAvailable() {
    final File parent = createDirectory("source");
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final ProjectDAO dao = Mockito.mock(ProjectDAO.class);
    Mockito.when(dao.find("project")).thenReturn(new Project());
    psi.setProjectDAO(dao);
    assertEquals("{\"error\" : \"Exception encountered while reading project settings\"}", psi.buildProject("project"));
    deleteDirectory(parent);
  }

  @Test
  public void buildSucceedsWithProperArgs() throws IOException {
    final File parent = createDirectory("source");
    final File project = new File(parent, "project");
    project.mkdir();
    final JsonObject settings = new JsonObject();
    settings.add("checkstyle", new Checkstyle().getDefaultSettings());
    final File settingsFile = new File(project, "settings.json");
    FileUtils.writeFileContents(settingsFile, settings.toString());

    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final ProjectDAO dao = Mockito.mock(ProjectDAO.class);
    Mockito.when(dao.find("project")).thenReturn(new Project());
    psi.setProjectDAO(dao);
    psi.setParentFile(parent);
    assertEquals("{\"error\" : \"none\"}", psi.buildProject("project"));
    deleteDirectory(parent);
  }

  @Test
  public void updateSettingsReturnsFalseWhenNameIsNull() {
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    assertFalse(psi.updateSettings(null, "settings"));
  }

  @Test
  public void updateSettingsReturnsFalseWhenSettingsIsNull() {
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    assertFalse(psi.updateSettings("name", null));
  }

  @Test
  public void updateSettingsReturnsFalseWhenProjectDoesNotExist() {
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final ProjectDAO dao = Mockito.mock(ProjectDAO.class);
    Mockito.when(dao.find("name")).thenReturn(null);
    psi.setProjectDAO(dao);
    assertFalse(psi.updateSettings("name", "settings"));
  }

  @Test
  public void updateSettingsReturnsFalseWhenSettingsIsInvalid() {
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final ProjectDAO dao = Mockito.mock(ProjectDAO.class);
    Mockito.when(dao.find("name")).thenReturn(new Project());
    psi.setProjectDAO(dao);
    assertFalse(psi.updateSettings("name", "settings"));
  }

  @Test
  public void updateSettingsTest() {
    final File parent = createDirectory("source");
    final File project = new File(parent, "project");
    project.mkdir();
    final File settingJson = new File(project, "settings.json");
    FileUtils.writeFileContents(settingJson, "{}");

    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final ProjectDAO dao = Mockito.mock(ProjectDAO.class);
    Mockito.when(dao.find("project")).thenReturn(new Project());
    psi.setProjectDAO(dao);
    psi.setParentFile(parent);

    assertTrue(psi.updateSettings("project", "{\"checkstyle\":{}}"));
    deleteDirectory(parent);
  }

  @Test
  public void updateSettingsTestv2() {
    final File parent = createDirectory("source");
    final File project = new File(parent, "project");
    project.mkdir();
    final File settingJson = new File(project, "settings.json");
    FileUtils.writeFileContents(settingJson, "{\"mkd\":{}}");

    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final ProjectDAO dao = Mockito.mock(ProjectDAO.class);
    Mockito.when(dao.find("project")).thenReturn(new Project());
    psi.setProjectDAO(dao);
    psi.setParentFile(parent);

    assertTrue(psi.updateSettings("project", "{\"checkstyle\":{}, \"pop\":{}}"));
    deleteDirectory(parent);
  }

  // how do you mock this?
  public void getToolsTest() {
    final ProjectServiceImpl psi = new ProjectServiceImpl();
    final String tools = psi.getTools();
    System.out.println(tools);
  }

  private File createDirectory(String parent) {
    final File file = new File(parent);
    file.mkdir();

    return file;
  }

  private void deleteDirectory(File directory) {
    try {
      FileUtils.deleteDirectoryRecursion(directory.toPath());
    } catch (final IOException e) {
      System.out.println("Error deleting directory");
    }
  }

}