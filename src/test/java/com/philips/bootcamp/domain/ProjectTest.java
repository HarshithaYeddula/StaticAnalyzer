package com.philips.bootcamp.domain;

import static org.junit.Assert.assertEquals;

import java.sql.Date;

import org.junit.Test;

public class ProjectTest {
    @Test
    public void projectGettersAndSetters() {
        Date date = new Date(System.currentTimeMillis());
        Project p = new Project();
        p.setName("name");
        p.setProjectCreationDate(date);
        p.setLastBuildDate(date);

        assertEquals("name", p.getName());
        assertEquals(date, p.getLastBuildDate());
        assertEquals(date, p.getProjectCreationDate());
    }
}