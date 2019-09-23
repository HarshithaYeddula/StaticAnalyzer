package com.philips.bootcamp.dal;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;

import com.philips.bootcamp.domain.Project;

import org.junit.Test;
import org.mockito.Mockito;

public class ProjectDAOImplTest {

    @Test
    public void save() {
        ProjectDAOImpl pdi = new ProjectDAOImpl();
        Project project = new Project();
        EntityManager em = Mockito.mock(EntityManager.class);

        pdi.setEntityManager(em);
        assertEquals(project, pdi.save(project));
    }

    @Test
    public void findProjectThatExists() {
        ProjectDAOImpl pdi = new ProjectDAOImpl();
        Project project = new Project();
        EntityManager em = Mockito.mock(EntityManager.class);
        Mockito.when(em.find(Project.class, "name")).thenReturn(project);

        pdi.setEntityManager(em);
        assertEquals(project, pdi.find("name"));
    }
    
    @Test
    public void findProjectThatDoesNotExists() {
        ProjectDAOImpl pdi = new ProjectDAOImpl();
        EntityManager em = Mockito.mock(EntityManager.class);
        Mockito.when(em.find(Project.class, "name")).thenReturn(null);

        pdi.setEntityManager(em);
        assertEquals(null, pdi.find("name"));
    }

    @Test
    public void deleteNonExistingProject() {
        ProjectDAOImpl pdi = new ProjectDAOImpl();
        EntityManager em = Mockito.mock(EntityManager.class);
        Mockito.when(em.find(Project.class, "name")).thenReturn(null);
        pdi.setEntityManager(em);
        pdi.delete("name");
    }

    @Test
    public void deleteExistingProject() {
        ProjectDAOImpl pdi = new ProjectDAOImpl();
        EntityManager em = Mockito.mock(EntityManager.class);
        Mockito.when(em.find(Project.class, "name")).thenReturn(new Project());
        pdi.setEntityManager(em);
        pdi.delete("name");
    }

    @Test
    public void updateNullProject() {
        ProjectDAOImpl pdi = new ProjectDAOImpl();
        EntityManager em = Mockito.mock(EntityManager.class);
        Mockito.when(em.merge(null)).thenReturn(null);
        pdi.setEntityManager(em);
        pdi.update(null);
    }

    
    @Test
    public void updateProject() {
        Project p = new Project();
        ProjectDAOImpl pdi = new ProjectDAOImpl();
        EntityManager em = Mockito.mock(EntityManager.class);
        Mockito.when(em.merge(p)).thenReturn(p);
        pdi.setEntityManager(em);
        pdi.update(p);
    }

}