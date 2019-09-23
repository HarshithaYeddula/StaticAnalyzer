/*
 * The copyright of this file belongs to Koninklijke Philips N.V., 2019.
 */
package com.philips.bootcamp.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.philips.bootcamp.domain.Project;
import com.philips.bootcamp.service.ProjectService;

@RestController
public class ProjectController {

  @Autowired
  ProjectService service;

  public void setService(ProjectService service) {
    this.service = service;
  }

  @RequestMapping(value = "/api/project", method = RequestMethod.GET)
  public List<Project> getProjectsList() {
    return service.findAll();
  }

  @RequestMapping(value = "/api/project/{name}", method = RequestMethod.GET)
  public ResponseEntity<Project> getProject(@PathVariable("name") String name) {
    final Project project = service.find(name);
    if (project == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<>(project, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/api/project", method = RequestMethod.POST)
  public ResponseEntity<Object> createNewProject(@RequestBody String project) {

    final String result = service.save(project);
    if (result.equals("Project created") || result.equals("Project updated")) {
      return new ResponseEntity<>(result, HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/api/project/{name}", method = RequestMethod.DELETE)
  public ResponseEntity<Object> deleteProject(@PathVariable("name") String name) {

    final boolean result = service.delete(name);
    ResponseEntity<Object> response = null;
    if (result) {
      response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } else {
      response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return response;
  }

  @RequestMapping(value = "/api/project/tool", method = RequestMethod.GET)
  public String getToolsList() {
    return service.getTools();
  }

  @RequestMapping(value = "/api/project/{name}/settings", method = RequestMethod.POST)
  public ResponseEntity<String> updateProjectSettings(@PathVariable("name") String project, @RequestBody String settings) {
    String result = service.updateSettings(project, settings);
    if (result.equals("Project settings updated")) {
      result="Project settings updated";
      return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
    } else if (result.equals("anObject")) {
      result="anObject";
      return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);

    }
    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);

  }

  @RequestMapping(value = "/api/project/{name}/settings", method = RequestMethod.GET)
  public ResponseEntity<String> getProjectSettings(@PathVariable("name") String name) {
    final List<String> errors = List.of("Project name required", "No project by name: " + name);
    final String settings = service.getSettings(name);
    if (errors.contains(settings)) {
      return new ResponseEntity<>(settings, HttpStatus.BAD_REQUEST);
    }
    if (settings.equals("File not present")) {
      return new ResponseEntity<>(settings, HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(settings, HttpStatus.OK);
  }

  @RequestMapping(value = "/api/project/{name}/report", method = RequestMethod.GET)
  public ResponseEntity<String> getProjectReport(@PathVariable("name") String name) {
    final List<String> errors = List.of("Project name required", "No project by name: " + name);
    final String report = service.getReport(name);
    if (errors.contains(report)) {
      return new ResponseEntity<>(report, HttpStatus.BAD_REQUEST);
    }
    if (report.equals("File not present")) {
      return new ResponseEntity<>(report, HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(report, HttpStatus.OK);
  }

  @RequestMapping(value = "/api/project/{name}/fence", method = RequestMethod.GET)
  public String fenceProject(@PathVariable("name") String name) throws Throwable {
    return service.fenceProject(name);
  }

  @RequestMapping(value = "/api/tool/{name}", method = RequestMethod.POST)
  public String getInstantReport(@PathVariable("name") String toolname, @RequestBody String source) {
    return service.getInstantReport(toolname, source);
  }
}