package utils.Jira;

import basepackage.BaseApi;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import constants.BasePath;
import io.restassured.response.Response;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import utils.GoogleSheets.SheetsServiceUtil;
import utils.LocalConfigs;
import utils.TestUtilFunctions;

/**
 * This helper class has helper functions for returning response of JQL
 */

public class QualityScoreCalculator {

  public static double priorityBasedScore(String priority)
    throws IOException, GeneralSecurityException {
    // get Score from priority[High(.25) /Med(.15)/ Low(.05)]
    double priorityScore = 0.00;
    if (priority.equalsIgnoreCase("medium")) priorityScore = .15; else if (
      priority.equalsIgnoreCase("high")
    ) priorityScore = .25; else priorityScore = .05;
    return priorityScore;
  }

  public static double fixTypeBasedScore(Response jqlResponse, int i)
    throws IOException, GeneralSecurityException {
    //Get Score for FixType [Won't Fix as Queries(.20) / Others as Code Fix(.60)]
    double fixTypeScore = 0.00;
    String fixType = "";

    try {
      fixType =
        TestUtilFunctions
          .getJsonValue(jqlResponse, "issues[" + i + "].fields.resolution.name")
          .toString();
    } catch (NullPointerException e) { //in case any required field comes up as blank
      fixType = "UnResolved";
    }

    String issueType = TestUtilFunctions
      .getJsonValue(jqlResponse, "issues[" + i + "].fields.issuetype.name")
      .toString();
    if (issueType.equalsIgnoreCase("bug")) {
      if (fixType.equals("Won't Fix")) fixTypeScore = .20; else fixTypeScore =
        .60;
    } else fixTypeScore = .20;
    return fixTypeScore;
  }
}
