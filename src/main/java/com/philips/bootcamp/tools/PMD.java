/*
 * The copyright of this file belongs to Koninklijke Philips N.V., 2019.
 */
package com.philips.bootcamp.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
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
import jdk.internal.org.jline.utils.Log;

public class PMD implements Tool {
  static final String ERRORS = "errors";
  static final String METRICS = "metrics";
  static final String REPORT = "report";
  static final String ERRORS_THEN = "errorsThen";
  static final String ERRORS_NOW = "errorsNow";
  static final String PERCENTAGE_CHANGE = "percentageChange";
  static final String RULESET = "ruleset";


  public JsonObject parseXml(String out) {
    if (out == null) {
      return null;
    }

    final JsonObject report = new JsonObject();
    final JsonObject metrics = new JsonObject();

    try {
      final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

      dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      final DocumentBuilder builder = dbFactory.newDocumentBuilder();
      final Document document = builder.parse(new ByteArrayInputStream(out.getBytes()));

      // recommended
      document.getDocumentElement().normalize();
      final NodeList files = document.getElementsByTagName("file");

      final int noOfViolations = document.getElementsByTagName("violation").getLength();
      metrics.addProperty(ERRORS, noOfViolations);

      for (int index = 0; index < files.getLength(); index++) {

        final JsonArray array = new JsonArray();

        final Node file = files.item(index);
        final NamedNodeMap attributes = file.getAttributes();
        final String fileLocation = attributes.getNamedItem("name").getNodeValue();
        Log.debug(fileLocation);

        final NodeList errors = ((Element) file).getElementsByTagName("violation");
        final int errorLength = errors.getLength();

        for (int eindex = 0; eindex < errorLength; eindex++) {
          final JsonObject object = new JsonObject();

          final Node error = errors.item(eindex);
          final NamedNodeMap errorDetails = error.getAttributes();
          object.addProperty("line", errorDetails.getNamedItem("beginline").getNodeValue());
          object.addProperty("priority", errorDetails.getNamedItem("priority").getNodeValue());
          object.addProperty("message", error.getTextContent());

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
    final StringBuilder command = new StringBuilder();
    command.append("\"" + new File(Constants.toolsDirectory, "pmd/bin/pmd.bat").getAbsolutePath() + "\"");
    command.append(" -d " + "\"" + settings.get("project").getAsString() + "\"");
    command.append(" -R " + settings.get(RULESET).getAsString());
    command.append(" -f xml");
    command.append(" -r " + "\"" + Constants.output.getAbsolutePath() + "\"");

    TerminalUtils.run(command.toString());
    return parseXml(FileUtils.getFileContents(Constants.output));
  }

  @Override
  public String getName() {
    return "pmd";
  }

  @Override
  public String getDescription() {
    return FileUtils.getFileContents(new File(Constants.toolsDirectory, "pmd.desc"));
  }

  @Override
  public boolean verifySettings(JsonObject settings) {
    final List<String> rulesets = List.of("rulesets/java/quickstart.xml");
    if (settings == null) {
      return false;
    }

    String value = null;
    if (settings.has(RULESET)) {
      value = settings.get(RULESET).getAsString();
      if (!rulesets.contains(value)) {
        ;
      }
      return false;
    }

    return true;
  }

  @Override
  public JsonObject getDefaultSettings() {
    final JsonObject defaults = new JsonObject();
    defaults.addProperty(RULESET, "rulesets/java/quickstart.xml");
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