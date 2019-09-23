/*
 * The copyright of this file belongs to Koninklijke Philips N.V., 2019.
 */
package com.philips.bootcamp.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import org.junit.Test;
import com.google.gson.JsonObject;
import com.philips.bootcamp.domain.Constants;
import com.philips.bootcamp.utils.FileUtils;

public class CheckstyleTest {

  @Test
  public void getNameReturnsCheckstyle() {
    final Checkstyle checkstyle = new Checkstyle();
    assertEquals("checkstyle", checkstyle.getName());
  }


  @Test
  public void getDescriptionReturnsContensOfCheckstyle_dot_desc() {
    final File checkstyleDesc = new File(Constants.toolsDirectory, "checkstyle.desc");
    final String checkstyleDescContents = FileUtils.getFileContents(checkstyleDesc);
    final Checkstyle checkstyle = new Checkstyle();
    assertEquals(checkstyleDescContents, checkstyle.getDescription());
  }

  @Test
  public void defaultSettingsReturnsDefaults() {
    final Checkstyle checkstyle = new Checkstyle();
    final JsonObject actualDefault = checkstyle.getDefaultSettings();

    final JsonObject expectedDefault = new JsonObject();
    expectedDefault.addProperty("styleguide", "google_checks");
    expectedDefault.addProperty("excludeTestFiles", "no");

    assertEquals(expectedDefault, actualDefault);
  }

  @Test
  public void verifySettingsReturnsFalseForNullArg() {
    final Checkstyle checkstyle = new Checkstyle();
    assertTrue(!checkstyle.verifySettings(null));
  }

  @Test
  public void verifySettingsIsPassedRandomSettingsAndItReturnsFalse() {
    final Checkstyle checkstyle = new Checkstyle();
    JsonObject settings = new JsonObject();
    settings.addProperty("styleguide", "nonsense");
    assertTrue(!checkstyle.verifySettings(settings));

    settings = new JsonObject();
    settings.addProperty("excludeTestFiles", "nonsense");
    assertTrue(!checkstyle.verifySettings(settings));
  }

  @Test
  public void verifySettingsIsPassedProperSettingsAndItReturnsTrue() {
    final Checkstyle checkstyle = new Checkstyle();
    JsonObject settings = new JsonObject();
    settings.addProperty("styleguide", "google_checks");
    assertTrue(checkstyle.verifySettings(settings));

    settings.addProperty("styleguide", "sun_checks");
    assertTrue(checkstyle.verifySettings(settings));

    settings = new JsonObject();
    settings.addProperty("excludeTestFiles", "no");
    assertTrue(checkstyle.verifySettings(settings));

    settings.addProperty("excludeTestFiles", "yes");
    assertTrue(checkstyle.verifySettings(settings));
  }

  //  @Test
  //  public void execute() {
  //    final Checkstyle cs = Mockito.mock(Checkstyle.class);
  //    final JsonObject settings = new JsonObject();
  //    settings.addProperty("styleguide", "sun_checks");
  //    settings.addProperty("excludeTestFiles", "no");
  //    settings.addProperty("project", "test");
  //    final JsonObject returnValue = new JsonObject();
  //    //Mockito.when(TerminalUtils.run("java -jar ./../tools/checkstyle-8.23-all.jar -c sun_checks.xml \"test\" -f xml -e target"))
  //    //    .thenReturn("value");
  //    Mockito.when(cs.parseXml("value")).thenReturn(returnValue);
  //    Mockito.when(cs.execute(settings)).thenCallRealMethod();
  //
  //    final JsonObject result = cs.execute(settings);
  //    assertEquals(returnValue, result);
  //  }

  //  @Test
  //  public void parseXml() {
  //    final Checkstyle cs = new Checkstyle();
  //    final String out = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
  //        "<checkstyle version=\"8.23\">" +
  //        "<file name=\"C:\\Program Files\\Apache Tomcat 8\\bin\\.\\..\\sources\\test\\Main.java\">" +
  //        "<error line=\"1\" severity=\"error\" message=\"File does not end with a newline.\"/>" +
  //        "</file>" +
  //        "</checkstyle>";
  //    final JsonObject result = cs.parseXml(out);
  //    final String expectedJson = "{\"C:\\\\Program Files\\\\Apache Tomcat 8\\\\bin\\\\.\\\\..\\\\sources\\\\test\\\\Main.java\":[{\"line\":\"1\",\"severity\":\"error\",\"message\":\"File does not end with a newline.\",\"metrics\":{\"errors\":\"1\"}]}";
  //    assertEquals(expectedJson, result.toString());
  //  }

  @Test
  public void nullForNull() {
    final Checkstyle cs = new Checkstyle();
    assertNull(cs.parseXml(null));
  }
}