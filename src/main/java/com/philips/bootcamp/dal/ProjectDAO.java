package com.philips.bootcamp.dal;

import java.util.List;

import com.philips.bootcamp.domain.Project;

public interface ProjectDAO {
    Project save(Project project);
    void update(Project project);
    List<Project> findAll();
    Project find(String name);
    void delete(String name);
}