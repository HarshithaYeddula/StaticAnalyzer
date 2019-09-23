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

public class MavenTest {

    @Test
    public void getNameReturnsCheckstyle() {
        Maven maven = new Maven();
        assertEquals("maven", maven.getName());
    }

    @Test
    public void getDescriptionReturnsContensOfCheckstyle_dot_desc() {
        File mavenDesc = new File(Constants.toolsDirectory, "maven.desc");
        String mavenDescContents = FileUtils.getFileContents(mavenDesc);
        Maven maven = new Maven();
        assertEquals(mavenDescContents, maven.getDescription());
    }

    @Test
    public void defaultSettingsReturnsDefaults() {
        Maven maven = new Maven();
        JsonObject actualDefault = maven.getDefaultSettings();
        
        JsonObject expectedDefault = new JsonObject();
        expectedDefault.addProperty("command", "mvn package");

        assertEquals(expectedDefault, actualDefault);
    }

    @Test
    public void verifySettingsReturnsFalseForNullArg() {
        Maven maven = new Maven();
        assertTrue(!maven.verifySettings(null));
    }

    @Test
    public void verifySettingsIsPassedRandomSettingsAndItReturnsFalse() {
        Maven maven = new Maven();
        JsonObject settings = new JsonObject();
        settings.addProperty("command", "nonsense");
        assertTrue(!maven.verifySettings(settings));
    }

    @Test
    public void verifySettingsIsPassedProperSettingsAndItReturnsTrue() {
        Maven maven = new Maven();
        JsonObject settings = new JsonObject();
        settings.addProperty("command", "mvn package");
        assertTrue(maven.verifySettings(settings));
    }

    @Test
    public void execute() {
        Maven maven = Mockito.mock(Maven.class);
        
        JsonObject settings = new JsonObject();
        settings.addProperty("project", "test");
        settings.addProperty("command", "mvn compile");

        JsonObject returnValue = new JsonObject();
        Mockito.when(maven.handleOutput("BUILD SUCCESSFUL")).thenReturn(returnValue);
        //Mockito.when(maven.run("\"./../tools/maven.bat\" \"test\" \"mvn compile\"")).thenReturn("BUILD SUCCESSFUL");
        Mockito.when(maven.execute(settings)).thenCallRealMethod();

        assertEquals(returnValue, maven.execute(settings));
    }

    @Test
    public void handleOutputForSuccess() {
        Maven maven = new Maven();
        String out = "info BUILD SUCCESS info";
        JsonObject result = new JsonObject();
        result.addProperty("buildStatus", "success");
        result.addProperty("details", out);

        assertEquals(result, maven.handleOutput(out));
    }
    
    @Test
    public void handleOutputForFailure() {
        Maven maven = new Maven();
        String out = "info BUILD FAILURE info";
        JsonObject result = new JsonObject();
        result.addProperty("buildStatus", "failure");
        result.addProperty("details", out);

        assertEquals(result, maven.handleOutput(out));
    }

    @Test
    public void nullForNull() {
        Maven maven = new Maven();
        assertNull(maven.handleOutput(null));
    }
}