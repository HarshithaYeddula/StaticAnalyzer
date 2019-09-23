/*
 * The copyright of this file belongs to Koninklijke Philips N.V., 2019.
 */
package com.philips.bootcamp.rest;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.philips.bootcamp.domain.Project;
import com.philips.bootcamp.service.ProjectService;

public class ProjectControllerTest {

  @Test
  public void getProjectsList() {
    final List<Project> projects = List.of(new Project());
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.findAll()).thenReturn(projects);
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    assertEquals(projects, controller.getProjectsList());
  }

  @Test
  public void getProjectNotExisting() {
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.find("project")).thenReturn(null);
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    assertEquals(HttpStatus.NOT_FOUND, controller.getProject("project").getStatusCode());
  }


  @Test
  public void getProjectExisting() {
    final Project project = new Project();
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.find("project")).thenReturn(project);
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    final ResponseEntity<Project> response = controller.getProject("project");
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(project, response.getBody());
  }

  @Test
  public void createNewProjectError() {
    final String project = "project";
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.save(project)).thenReturn("Project json must have property 'link'");
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    assertEquals(HttpStatus.BAD_REQUEST, controller.createNewProject(project).getStatusCode());
  }



  @Test
  public void createnewProjectSuccess() {
    final String project = "project";
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.save(project)).thenReturn("Project created");
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    assertEquals(HttpStatus.CREATED, controller.createNewProject(project).getStatusCode());
  }

  @Test
  public void deleteNonxistingProject() {
    final String project = "project";
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.delete(project)).thenReturn(false);
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    assertEquals(HttpStatus.NOT_FOUND, controller.deleteProject(project).getStatusCode());
  }

  @Test
  public void deleteExistigProject() {
    final String project = "project";
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.delete(project)).thenReturn(true);
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    assertEquals(HttpStatus.NO_CONTENT, controller.deleteProject(project).getStatusCode());
  }

  @Test
  public void getToolsList() {
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.getTools()).thenReturn("tools");
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    assertEquals("tools", controller.getToolsList());
  }

  @Test
  public void updateProjectSettingsSuccessful() {
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.updateSettings("project", "settings")).thenReturn("Project settings updated");
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    assertEquals(HttpStatus.NO_CONTENT, controller.updateProjectSettings("project", "settings").getStatusCode());
  }

  @Test
  public void errorInUpdatingProjectSettings() {
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.updateSettings("project", "settings")).thenReturn("Invalid settings json string");
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    assertEquals(HttpStatus.BAD_REQUEST, controller.updateProjectSettings("project", "settings").getStatusCode());
  }


  @Test
  public void getProjectSettingsWhenProjectExists() {
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.getSettings("project")).thenReturn("settings");
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    final ResponseEntity<String> response = controller.getProjectSettings("project");
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("settings", response.getBody());
  }

  @Test
  public void getProjectSettingsWhenFileDoesnotExist() {
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.getSettings("project")).thenReturn("File not present");
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    final ResponseEntity<String> response = controller.getProjectSettings("project");
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("File not present", response.getBody());
  }



  @Test
  public void getProjectSettingsWhenProjectDoesNotExist() {
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.getSettings("project")).thenReturn("No project by name: project");
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    final ResponseEntity<String> response = controller.getProjectSettings("project");
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void getProjectReportWhenProjectExists() {
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.getReport("project")).thenReturn("report");
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    final ResponseEntity<String> response = controller.getProjectReport("project");
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("report", response.getBody());
  }

  @Test
  public void getProjectReportWhenFileDoesnotExist() {
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.getReport("project")).thenReturn("File not present");
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    final ResponseEntity<String> response = controller.getProjectReport("project");
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("File not present", response.getBody());
  }


  @Test
  public void getProjectReportWhenProjectDoesNotExist() {
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.getReport("project")).thenReturn("No project by name: project");
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    final ResponseEntity<String> response = controller.getProjectReport("project");
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void buildProject() throws Throwable {
    final ProjectService ps = Mockito.mock(ProjectService.class);
    Mockito.when(ps.fenceProject("project")).thenReturn("value");
    final ProjectController controller = new ProjectController();
    controller.setService(ps);

    assertEquals("value", controller.fenceProject("project"));
  }

}