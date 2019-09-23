/*
 * The copyright of this file belongs to Koninklijke Philips N.V., 2019.
 */
package com.philips.bootcamp.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.philips.bootcamp.domain.Constants;
import com.philips.bootcamp.domain.Tool;
import com.philips.bootcamp.utils.FileUtils;
import com.philips.bootcamp.utils.TerminalUtils;

public class Checkstyle implements Tool {
  static final String STYLEGUIDE = "styleguide";
  static final String EXCLUDE_TEST_FILES = "excludeTestFiles";
  static final String ERRORS = "errors";
  static final String METRICS = "metrics";
  static final String REPORT = "report";
  static final String ERRORS_THEN = "errorsThen";
  static final String ERRORS_NOW = "errorsNow";
  static final String PERCENTAGE_CHANGE = "percentageChange";



  public JsonObject parseXml(String out) {


    if (out == null) {
      return null;
    }

    int noOfErrors = 0;
    final JsonObject report = new JsonObject();
    final JsonObject metrics = new JsonObject();

    try {
      final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();


      dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);



      final DocumentBuilder builder = dbFactory.newDocumentBuilder();
      final Document document = builder.parse(new ByteArrayInputStream(out.getBytes()));
      document.getDocumentElement().normalize();
      final NodeList files = document.getElementsByTagName("file");

      noOfErrors = document.getElementsByTagName("error").getLength();
      metrics.addProperty(ERRORS, noOfErrors);

      for (int index = 0; index < files.getLength(); index++) {

        final JsonArray array = new JsonArray();

        final Node file = files.item(index);
        final NamedNodeMap attributes = file.getAttributes();
        final String fileLocation = attributes.getNamedItem("name").getNodeValue();


        final NodeList errors = ((Element) file).getElementsByTagName("error");
        final int errorLength = errors.getLength();

        for (int eindex = 0; eindex < errorLength; eindex++) {
          final JsonObject object = new JsonObject();

          final Node error = errors.item(eindex);
          final NamedNodeMap errorDetails = error.getAttributes();
          object.addProperty("line", errorDetails.getNamedItem("line").getNodeValue());
          object.addProperty("severity", errorDetails.getNamedItem("severity").getNodeValue());
          object.addProperty("message", errorDetails.getNamedItem("message").getNodeValue());

          array.add(object);
        }

        report.add(fileLocation, array);
      }

    } catch (ParserConfigurationException | SAXException | IOException e) {
      return null;
    }

    final JsonObject data = new JsonObject();
    data.add(REPORT, report);
    data.add(METRICS, metrics);
    return data;
  }

  @Override
  public JsonObject execute(JsonObject settings) {


    final StringBuilder command = new StringBuilder("java -jar");
    command.append(" \"" + new File(Constants.toolsDirectory, "checkstyle-8.23-all.jar").getAbsolutePath() + "\"");
    command.append(" -c " + settings.get(STYLEGUIDE).getAsString() + ".xml");

    command.append(" \"" + settings.get("project").getAsString() + "\"");

    command.append(" -f xml");


    command.append(" -o \"" + Constants.output.getAbsolutePath() + "\"");

    command.append(" -e target");

    if (settings.get(EXCLUDE_TEST_FILES).getAsString().equals("yes")) {
      command.append(" -e src/test");
    }


    TerminalUtils.run(command.toString());

    return parseXml(FileUtils.getFileContents(Constants.output));
  }

  @Override
  public String getName() {
    return "checkstyle";
  }

  @Override
  public String getDescription() {
    return FileUtils.getFileContents(new File(Constants.toolsDirectory, "checkstyle.desc"));
  }

  @Override
  public boolean verifySettings(JsonObject settings) {
    if (settings == null) {
      return false;
    }

    String value = null;

    if (settings.has(STYLEGUIDE)) {
      value = settings.get(STYLEGUIDE).getAsString();
      if (value.equals("sun_checks") || value.equals("google_checks")) {
        ;
      }
      else {
        return false;
      }
    }

    if (settings.has(EXCLUDE_TEST_FILES)) {
      value = settings.get(EXCLUDE_TEST_FILES).getAsString();
      if (value.equals("yes") || value.equals("no")) {
        ;
      } else {
        return false;
      }
    }

    return true;
  }

  @Override
  public JsonObject getDefaultSettings() {
    final JsonObject defaults = new JsonObject();
    defaults.addProperty(STYLEGUIDE, "google_checks");
    defaults.addProperty(EXCLUDE_TEST_FILES, "no");
    return defaults;
  }

  @Override
  public JsonObject compare(JsonObject futureReport, JsonObject pastReport) {
    final JsonObject comparison = new JsonObject();
    double percentage = 0.0;

    final DecimalFormat df = new DecimalFormat("#.###");

    if (pastReport != null && futureReport != null) {
      final int errorsThen = pastReport.get(METRICS).getAsJsonObject().get(ERRORS).getAsInt();
      final int errorsNow = futureReport.get(METRICS).getAsJsonObject().get(ERRORS).getAsInt();

      if (errorsThen != -1) {
        percentage = (errorsThen - errorsNow) * 1f / (errorsThen * 1f);
      }

      comparison.addProperty(ERRORS_THEN, errorsThen);
      comparison.addProperty(ERRORS_NOW, errorsNow);
      comparison.addProperty(PERCENTAGE_CHANGE, df.format(percentage));

      return comparison;

    } else if (pastReport == null && futureReport != null) {
      final int errorsNow = futureReport.get(METRICS).getAsJsonObject().get(ERRORS).getAsInt();

      comparison.addProperty(ERRORS_THEN, "null");
      comparison.addProperty(ERRORS_NOW, errorsNow);
      comparison.addProperty(PERCENTAGE_CHANGE, "null");

      return comparison;
    } else {
      comparison.addProperty(ERRORS_THEN, "null");
      comparison.addProperty(ERRORS_NOW, "null");
      comparison.addProperty(PERCENTAGE_CHANGE, "null");

      return comparison;
    }
  }
}