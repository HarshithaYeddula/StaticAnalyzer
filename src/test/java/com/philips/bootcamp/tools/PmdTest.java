package com.philips.bootcamp.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import com.google.gson.JsonObject;
import com.philips.bootcamp.domain.Constants;
import com.philips.bootcamp.utils.FileUtils;

import org.junit.Test;
import org.mockito.Mockito;

public class PmdTest {

    @Test
    public void getNameReturnsCheckstyle() {
        PMD pmd = new PMD();
        assertEquals("pmd", pmd.getName());
    }

    @Test
    public void getDescriptionReturnsContensOfCheckstyle_dot_desc() {
        File pmdDesc = new File(Constants.toolsDirectory, "pmd.desc");
        String pmdDescContents = FileUtils.getFileContents(pmdDesc);
        PMD pmd = new PMD();
        assertEquals(pmdDescContents, pmd.getDescription());
    }

    @Test
    public void defaultSettingsReturnsDefaults() {
        PMD pmd = new PMD();
        JsonObject actualDefault = pmd.getDefaultSettings();
        
        JsonObject expectedDefault = new JsonObject();
        expectedDefault.addProperty("ruleset", "rulesets/java/quickstart.xml");

        assertEquals(expectedDefault, actualDefault);
    }

    @Test
    public void verifySettingsReturnsFalseForNullArg() {
        PMD pmd = new PMD();
        assertTrue(!pmd.verifySettings(null));
    }

    @Test
    public void verifySettingsIsPassedRandomSettingsAndItReturnsFalse() {
        PMD pmd = new PMD();
        JsonObject settings = new JsonObject();
        settings.addProperty("ruleset", "nonsense");
        assertTrue(!pmd.verifySettings(settings));
    }

    @Test
    public void verifySettingsIsPassedProperSettingsAndItReturnsTrue() {
        PMD pmd = new PMD();
        JsonObject settings = new JsonObject();
        settings.addProperty("ruleset", "rulesets/java/quickstart.xml");
        assertTrue(pmd.verifySettings(settings));
    }

    @Test
    public void execute() {
        PMD pmd = Mockito.mock(PMD.class);
        JsonObject settings = new JsonObject();
        settings.addProperty("ruleset", "rulesets/java/quickstart.xml");
        settings.addProperty("project", "test");
        JsonObject returnValue = new JsonObject();
        //Mockito.when(pmd.run("\"./../tools/pmd/bin/pmd.bat\" -d \"test\" -R rulesets/java/quickstart.xml -f xml"))
        //    .thenReturn("value");
        Mockito.when(pmd.parseXml("value")).thenReturn(returnValue);
        Mockito.when(pmd.execute(settings)).thenCallRealMethod();

        JsonObject result = pmd.execute(settings);
        assertEquals(returnValue, result);
    }

    @Test 
    public void parseXml() {
        PMD pmd = new PMD();
        String out = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<pmd xmlns=\"http://pmd.sourceforge.net/report/2.0.0\"" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            " xsi:schemaLocation=\"http://pmd.sourceforge.net/report/2.0.0 http://pmd.sourceforge.net/report_2_0_0.xsd\"" +
            " version=\"6.17.0\" timestamp=\"2019-09-16T15:10:07.443\">" +
        " <file name=\"C:\\Program Files\\Apache Tomcat 8\\sources\\test\\Main.java\">" +
        " <violation beginline=\"1\" priority=\"3\">" +
        " All classes, interfaces, enums and annotations must belong to a named package" +
        " </violation>" + 
        " </file>" +
        " </pmd>";

        String expectedJson = "{\"C:\\\\Program Files\\\\Apache Tomcat 8\\\\sources\\\\test\\\\Main.java\":[{\"line\":\"1\",\"priority\":\"3\",\"message\":\" All classes, interfaces, enums and annotations must belong to a named package \"}]}";
        JsonObject result = pmd.parseXml(out);
        assertEquals(expectedJson, result.toString());
    }

    @Test
    public void nullForNull() {
        PMD pmd = new PMD();
        assertNull(pmd.parseXml(null));
    }
}