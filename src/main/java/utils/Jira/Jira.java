package utils.Jira;

import basepackage.BaseApi;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import constants.BasePath;
import io.restassured.response.Response;
import requestpojo.Slack.JiraCreate;
import utils.GoogleSheets.SheetsServiceUtil;
import utils.LocalConfigs;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * This helper class has helper functions for returning response of JQL
 */

public class Jira {

    static int assigneeMarker = 0;
    private static String SPREADSHEET_ID ="";  //Actual Sheet
    private static String JENKINS_SPREADSHEET_ID ="";
    private static Sheets sheetsService;

    /**
     * Returns API Response for JQL query
     */
    public static Response getJQLSearchResults(String query) {
        Response response=null;
        try {
            BaseApi api = new BaseApi();
            api.setRequestParams(
                    BaseApi.MethodType.GET,
                    LocalConfigs.baseURI,
                    BasePath.JIRA_SEARCH
            );
            HashMap queryParamsMap = new HashMap();
            queryParamsMap.put("jql", query);
            api.addQueryParam(queryParamsMap);
            HashMap headers = new HashMap();
            headers.put("Authorization", "");
            api.setCustomHeaders(headers);
            response= api.execute();

        }
        catch (Exception e){
            System.out.println("JQL Error Response:" +response.asString());
        }
        return response;

    }

    /**
     * Returns API Response for JQL query
     */
    public static Response getCommentsForKey(String key) {
        Response response=null;
        try {
            BaseApi api = new BaseApi();
            api.setRequestParams(
                    BaseApi.MethodType.GET,
                    LocalConfigs.baseURI,
                    BasePath.JIRA_COMMENTS+"/"+key+"/comment"
            );
            /*HashMap queryParamsMap = new HashMap();
            queryParamsMap.put("jql", query);
            api.addQueryParam(queryParamsMap);*/
            HashMap headers = new HashMap();
            headers.put("Authorization", "");
            api.setCustomHeaders(headers);
            response= api.execute();

        }
        catch (Exception e){
            System.out.println("JQL Error Response:" +response.asString());
        }
        return response;

    }

    public static String getBugAssignee(String jiraKey, String Description, String Summary) throws IOException, GeneralSecurityException {
        String finalAssignee = "";
        String SummaryPlusDesc = Summary+" "+Description;
        //Assignment Bias Logic
        if (jiraKey.contains("PC"))
            finalAssignee = "arjit.nigam@wingify.com";
        else if (Summary.toLowerCase().contains("preview") || Summary.toLowerCase().contains("editor") || (Summary.toLowerCase().contains("changes") && Summary.toLowerCase().contains("applied"))) {
            finalAssignee = RoundRobinAssignment("EditorRoundRobin!A1:Z100");
        }
        else if (Summary.toLowerCase().contains("scrollmap") || Summary.toLowerCase().contains("recording") || (SummaryPlusDesc.toLowerCase().contains("jslib") || SummaryPlusDesc.toLowerCase().contains("heat"))) {
            finalAssignee = RoundRobinAssignment("JsLibRoundRobin!A1:Z100");
        }
        else if (SummaryPlusDesc.contains(" GA ")  || SummaryPlusDesc.contains("GTM")  || SummaryPlusDesc.contains(" UA ")  || SummaryPlusDesc.toLowerCase().contains("integration") ) {
            finalAssignee = "himanshu.pushkar@wingify.com";
        }
        else if (SummaryPlusDesc.toLowerCase().contains("page load time")  || SummaryPlusDesc.toLowerCase().contains("page speed issue")  || SummaryPlusDesc.toLowerCase().contains("psi")  || SummaryPlusDesc.toLowerCase().contains("console error")  || SummaryPlusDesc.toLowerCase().contains("blocking")  || SummaryPlusDesc.toLowerCase().contains("high page")  || Description.toLowerCase().contains("performance") ) {
            finalAssignee = "mohit.khanna@wingify.com";
        }
        else if (SummaryPlusDesc.toLowerCase().contains("vwo osc")  || SummaryPlusDesc.toLowerCase().contains("smartcode")  || SummaryPlusDesc.toLowerCase().contains("vwo code")  ) {
            finalAssignee = "mohit.khanna@wingify.com";
        }
        else {
            finalAssignee = RoundRobinAssignment("Sheet1!A1:Z100");
        }

        return finalAssignee;
    }

    static String RoundRobinAssignment(String sheetName) throws IOException, GeneralSecurityException {
        sheetsService = SheetsServiceUtil.getSheetsService();
        ValueRange resultSet = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, sheetName).execute();
        List<List<Object>> data = resultSet.getValues();
        String assignee = "";
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).get(1).equals("0") && data.get(i).get(2).toString().toLowerCase().equals("present")) {
                assignee = data.get(i).get(0).toString();
                //Code to write and change 0 to 1e
                data.set(i, Arrays.asList(data.get(i).get(0), "1", data.get(i).get(2)));
                writeDataToRange(data, sheetName);
                break;
            }
            //Handle the case when one round of assignment is done and all are marked as 1
            if (i == data.size() - 1) {
                int k = 1;
                for (k = 1; k < data.size(); k++) {
                    if (data.get(k).get(2).toString().toLowerCase().equals("present")) {
                        assignee = data.get(k).get(0).toString();
                        break;
                    }
                }

                // write all to 0 from 1 except the one on first index
                for (int j = k + 1; j < data.size(); j++) {
                    data.set(j, Arrays.asList(data.get(j).get(0), "0", data.get(j).get(2)));
                }
                writeDataToRange(data, sheetName);
            }
        }
        return assignee;
    }

    static void writeDataToRange(List<List<Object>> data, String range) throws IOException {
        ValueRange body = new ValueRange()
                .setValues(data);
        UpdateValuesResponse result = sheetsService.spreadsheets().values()
                .update(SPREADSHEET_ID, range, body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }

    public static Boolean CheckAlreadyAssignedStatus(String bugID) throws IOException, GeneralSecurityException {
        sheetsService = SheetsServiceUtil.getSheetsService();
        ValueRange resultSet = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, "Sheet2!A1:Z100").execute();
        List<List<Object>> data = resultSet.getValues();
        Boolean alreadyAssigned = false;
        for (int i = 0; i < data.size(); i++)
            if (data.get(i).get(0).toString().equalsIgnoreCase(bugID)) {
                alreadyAssigned = true;
                break;
            }
        return alreadyAssigned;
    }

    public static void addAssignmentHistory(String bugID, String assignee) throws IOException, GeneralSecurityException {
        sheetsService = SheetsServiceUtil.getSheetsService();
        ValueRange resultSet = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, "Sheet2!A1:Z100").execute();
        List<List<Object>> data = resultSet.getValues();
        data.add(Arrays.asList(bugID, assignee));
        writeDataToRange(data, "Sheet2!A1:Z100");
    }

    public static List<List<Object>> getJenkinsAlertsFromSheet() throws IOException, GeneralSecurityException {
        sheetsService = SheetsServiceUtil.getSheetsService();
        ValueRange resultSet = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, "Alerts!A1:Z100").execute();
        List<List<Object>> data = resultSet.getValues();
        return data;
    }

    public static void addCalculatedQualityScoreToSheet(String weekRange, String score) throws IOException, GeneralSecurityException {
        sheetsService = SheetsServiceUtil.getSheetsService();
        ValueRange resultSet = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, "qualityScore!A1:Z100").execute();
        List<List<Object>> data = resultSet.getValues();
        data.add(Arrays.asList(weekRange, score));
        writeDataToRange(data, "qualityScore!A1:Z100");
    }
    public static Response createIssue(String desc, String summary) {
        Response response = null;
        try {
            BaseApi api = new BaseApi();
            JiraCreate jiraCreate = new JiraCreate();
            JiraCreate.Fields fields = new JiraCreate.Fields();
            JiraCreate.Issuetype issuetype = new JiraCreate.Issuetype();
            issuetype.setId(10500);
            JiraCreate.Project project = new JiraCreate.Project();
            project.setId(10004);
            fields.setDescription(
                    summary +
                            "\n" +
                            "\n" +
                            "This Bug is created from a mail received at bugs@wingify.com"
            );
            fields.setSummary(desc);
            fields.setIssuetype(issuetype);
            fields.setProject(project);
            fields.setReporter(new JiraCreate.Fields.Reporter());
            jiraCreate.setFields(fields);
            api.setRequestParams(
                    jiraCreate,
                    BaseApi.MethodType.POST,
                    LocalConfigs.baseURI,
                   BasePath.JIRA_COMMENTS
            );
            HashMap queryParamsMap = new HashMap();
            api.addQueryParam(queryParamsMap);
            HashMap headers = new HashMap();
            headers.put(
                    "Authorization",
                    ""
            );
            api.setCustomHeaders(headers);
            response = api.execute();
        } catch (Exception e) {
            System.out.println("JQL Error Response:" + response.asString());
        }
        return response;
    }
}
