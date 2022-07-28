package testclasses;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import utils.Jira.Jira;
import utils.Jira.QualityScoreCalculator;
import utils.Jira.SlackUtils;
import utils.Jira.checkEmail;
import utils.TestUtilFunctions;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JiraAPIs {

    final static String channelName = System.getProperty("channel");
  	final static String host = "imap.gmail.com";

    @Test(groups = "g1")
    private static void PushLongPendingClientIssuesToSlack() {
        String query = "status in (Open, \"In Progress\", \"In Review\", \"In QA\", Reopened, Resolved," +
                " \"Pending Deployment for QA\") AND priority in (High, Medium) AND \"Custom Labels\" = customer AND" +
                " created >= -24w AND cf[10302] not in (blockedOnCustomer,blockedOnThirdParty, fixWithDataLayer, blockedOnPMPriortisation," +
                " gatingCROPlatformGeneralAvailability, toMonitor, customerIssuesBacklogTill2017) AND created >= 2020-11-01" +
                " AND labels not in (blockedOnCHES) AND Created < -7d AND issuetype = Bug ";

        Response jqlResponse = Jira.getJQLSearchResults(query);
        TestUtilFunctions.validateStatusCode(jqlResponse, 200);
        String totalIssues = TestUtilFunctions.getJsonValue(jqlResponse, "total").toString();
        int count = Integer.parseInt(totalIssues);
        Set<String> assignee = new HashSet<>();
        int issueCountWithNoSprintValue=0;
        for (int i = 0; i < count; i++) {
            try {
                //get userId for each assignee
                String email = TestUtilFunctions.getJsonValue(jqlResponse, "issues[" + i + "].fields.assignee.name").toString();
                //code to get count of issues with no sprint value
                if (Objects.isNull(TestUtilFunctions.getJsonValue(jqlResponse, "issues[" + i + "].fields.customfield_10005"))){
                    issueCountWithNoSprintValue++;
                }
                String userId = SlackUtils.getUserIDbyEmail(email);
                userId = "<@" + userId + ">";
                assignee.add(userId);

            } catch (NullPointerException e) { // for the scenario where there is no assignee at the moment
                e.printStackTrace();
            }
        }
        String assigneeList = "";
        List<String> stringsList = new ArrayList<>(assignee);
        for (int j = 0; j < stringsList.size(); j++) {
            assigneeList = assigneeList + " " + (stringsList.get(j)) + "\n";
        }
        // Step -2 Build the Message body
        String message = "<!here> There are a total of " + totalIssues + " Client issues that are in open state for more than 7 days:" + "\n" + " Please Update/follow up/close the ones assigned to you." + "\n" + assigneeList + "\n\n\n" + "https://jira.wingify.com/issues/?filter=14501     *"+issueCountWithNoSprintValue+" out of these do not have a sprint value assigned as yet.*";
        //Step-3 Push message to Slack
        Response slackMessage = SlackUtils.sendMessageToSlack(channelName, message);
        TestUtilFunctions.validateStatusCode(slackMessage, 200);
    }

    @Test(groups = "g2")
    private static void PushDebugClientIssueAssignmentToSlack() throws IOException, GeneralSecurityException {
        String query = "issuetype = ZenDeskTicket AND status not in (Closed, Resolved)";
        Response jqlResponse = Jira.getJQLSearchResults(query);
        TestUtilFunctions.validateStatusCode(jqlResponse, 200);
        int totalIssues = ((Integer) TestUtilFunctions.getJsonValue(jqlResponse, "total"));
        //HashMap<String, String> bugDetails = new HashMap<>();
        for (int i = 0; i < totalIssues; i++) {
            String issueKey = TestUtilFunctions.getJsonValue(jqlResponse, "issues[" + i + "].key").toString();
            String issueDescription = TestUtilFunctions.getJsonValue(jqlResponse, "issues[" + i + "].fields.description").toString();
            String issueSummary = TestUtilFunctions.getJsonValue(jqlResponse, "issues[" + i + "].fields.summary").toString();
            if (!Jira.CheckAlreadyAssignedStatus(issueKey)) {           // skip if the bug is already assigned
                String assignee = Jira.getBugAssignee(issueKey, issueDescription, issueSummary);

                // Step -2 Build the Message body
               String userId = SlackUtils.getUserIDbyEmail(assignee);
                userId = "<@" + userId + ">";

                String IssueDescription =TestUtilFunctions.getJsonValue(jqlResponse, "issues[" + i + "].fields.description").toString();
                //temp fix to esclate WhitEHatJr issues
                if (IssueDescription.contains("509330")) {
                    userId = userId+" <@U011ERWQZ7G>";
                }

                String messageToSlack = issueKey + ": " + issueSummary + "   " + userId;

                //Step-3 Push message to Slack
                Response slackMessage = SlackUtils.sendMessageToSlack(channelName, messageToSlack);
                TestUtilFunctions.validateStatusCode(slackMessage, 200);
                // writing a bug to the already assigned sheet once assigned
                Jira.addAssignmentHistory(issueKey, assignee);
            }
        }
    }

    @Test(groups = "g3")
    private static void PushJenkinsJobAlertsToClack() throws IOException, GeneralSecurityException {

        List<List<Object>> data = Jira.getJenkinsAlertsFromSheet();

        for (int i=1; i<data.size(); i++){

            String assignee = data.get(i).get(1).toString();
            String userId = SlackUtils.getUserIDbyEmail(assignee);
            userId = "<@" + userId + ">";
            String alert = "";
            String alertType = "";
            if (data.get(i).get(3).toString().equals("")){
                //kudos Alert Here
                alert = data.get(i).get(4).toString();
                alertType ="Kudos ! ";
            }
            else{
                //Failure Alert Here
                alert = data.get(i).get(3).toString();
                alertType ="Alert ! ";
            }

            String messageToSlack = alertType+userId+" --> "+data.get(i).get(0).toString()+" --> "+alert;
            Response slackMessage = SlackUtils.sendMessageToSlack(channelName, messageToSlack);
            TestUtilFunctions.validateStatusCode(slackMessage, 200);
        }
    }

    @Test(groups = "qualityScore")
    private static void CalculateQualityScore() throws GeneralSecurityException, IOException {

        for (int toWeek=52; toWeek >0;toWeek--) {
            int fromWeek=toWeek+1;
            String query = " created >= -"+fromWeek+"w AND Created < -"+toWeek+"w AND status in (Open, \"In Progress\", \"In Review\", \"In QA\", Reopened, Resolved, \"Pending Deployment for QA\", Closed) AND \"Custom Labels\" = customer AND \"Product Area\" =jsLib ORDER BY key ASC";
            Response jqlResponse = Jira.getJQLSearchResults(query);
            TestUtilFunctions.validateStatusCode(jqlResponse, 200);
            String totalIssues = TestUtilFunctions.getJsonValue(jqlResponse, "total").toString();
            int count = Integer.parseInt(totalIssues);

            Set<String> assignee = new HashSet<>();
            double totalScore = 0;
            for (int i = 0; i < count; i++) {
                try {
                    //calculate penalty for each bug in this loop
                    double localScore = 0.00;
                    double priorityScore = 0.00;
                    double fixTypeScore = 0.00;


                    /*String priority = TestUtilFunctions.getJsonValue(jqlResponse, "issues[" + i + "].fields.priority.name").toString();
                    priorityScore = QualityScoreCalculator.priorityBasedScore(priority);*/

                    //Score based on Bug/Task Count
                    fixTypeScore = QualityScoreCalculator.fixTypeBasedScore(jqlResponse,i);

                    System.out.println("============="+priorityScore+"=================" +fixTypeScore);
                    localScore = priorityScore + fixTypeScore;
                    totalScore = totalScore + localScore;

                } catch (NullPointerException e) { //in case any required field comes up as blank
                    System.out.println("+++++++++++++++++++++++" + TestUtilFunctions.getJsonValue(jqlResponse, "issues[" + i + "].key").toString());
                    e.printStackTrace();
                }
            }
            String weekRange = toWeek+" to "+fromWeek+" ago";
            Jira.addCalculatedQualityScoreToSheet(weekRange,Double.toString(totalScore));
        }
    }

    @Test(groups = "ClientIssueSLA")
    private static void SlaMissedAlerts() throws GeneralSecurityException, IOException {

            //String query = "project = QF AND created >= 2020-10-01 AND created <= 2021-04-01 AND reporter in (\"pratik.sisodia@wingify.com\")";
        String query = "project = QF AND created >= 2020-10-01 AND created <= 2021-04-01 AND reporter in (\"pratik.sisodia@wingify.com\")";
            //String query = "issuekey=QF-9437";
            Response jqlResponse = Jira.getJQLSearchResults(query);
            TestUtilFunctions.validateStatusCode(jqlResponse, 200);
            String totalIssues = TestUtilFunctions.getJsonValue(jqlResponse, "total").toString();
            int count = Integer.parseInt(totalIssues);
            String listOfSlaMisses ="";

           for (int i = 0; i < count; i++) {
                try {
                   //get created Date for this issue
                   // get comment date
                   //add key to a list in case SLA not met
                    String key = TestUtilFunctions.getJsonValue(jqlResponse, "issues["+i+"].key").toString();
                    String issueCreatedDate = TestUtilFunctions.getJsonValue(jqlResponse, "issues["+i+"].fields.created").toString();
                    Response jqlResponseComments = Jira.getCommentsForKey(key);
                    String commentCreatedDate = TestUtilFunctions.getJsonValue(jqlResponseComments, "comments[0].created").toString();
                    String firstCommentAuthor = TestUtilFunctions.getJsonValue(jqlResponseComments, "comments[0].author.emailAddress").toString();

                    SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date issueCreatedDate1=formatter.parse(issueCreatedDate);
                    Date commentCreatedDate1=formatter.parse(commentCreatedDate);

                    //find hours to next working day block starts
                    Calendar c = Calendar.getInstance();
                    c.setTime(issueCreatedDate1);
                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                    int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
                    System.out.println(dayOfWeek+"======================================="+hourOfDay);
                    int xHours = 0;
                        if (dayOfWeek==2 || dayOfWeek==3 || dayOfWeek==4 || dayOfWeek==5){
                            if (hourOfDay >12 ){
                                xHours =24-hourOfDay+11;
                            }
                            else if (12-hourOfDay<4 ){
                                xHours = 5+11-(12-hourOfDay);
                            }
                            else
                                xHours=0;
                        }
                        if (dayOfWeek==6){
                            if (hourOfDay >12 ){
                                xHours =72-hourOfDay+11;
                            }
                            else if (12-hourOfDay<4 ){
                                xHours = 72+5+11-(12-hourOfDay);
                            }
                            else
                                xHours=0;
                        }
                        if (dayOfWeek==1){
                            xHours =24-hourOfDay+11;
                        }
                        if (dayOfWeek==7){
                        xHours =48-hourOfDay+11;
                        }

                    //find hours to next working day block ends
                    long difference_In_Time
                            = commentCreatedDate1.getTime() - issueCreatedDate1.getTime();
                    long difference_In_Hours
                            = ((difference_In_Time
                            / (1000 * 60 * 60))
                            % 24)-xHours;
                    if (difference_In_Hours >4){
                        System.out.println(difference_In_Hours);
                        listOfSlaMisses = listOfSlaMisses+key+",";
                    }
                    System.out.println(i+"==============================="+listOfSlaMisses);
                } catch (NullPointerException | ParseException e) { //in case any required field comes up as blank
                    System.out.println("+++++++++++++++++++++++" + TestUtilFunctions.getJsonValue(jqlResponse, "issues[" + i + "].key").toString());
                    e.printStackTrace();
                }
            }
        System.out.println(listOfSlaMisses);
        //Step-3 Push message to Slack
        String messageToSlack = "Possible first response  SLA Misses: "+listOfSlaMisses +" <@U011ERWQZ7G>";
        Response slackMessage = SlackUtils.sendMessageToSlack(channelName, messageToSlack);
        TestUtilFunctions.validateStatusCode(slackMessage, 200);
    }


    @Test(groups = "AlertClientIssueSLA")
    private static void SlaMissedAlertsToSlack() throws GeneralSecurityException, IOException {

        //String query = "project = QF AND created >= 2020-10-01 AND created <= 2021-04-01 AND reporter in (\"pratik.sisodia@wingify.com\")";
        String query = "project = QF AND created >= 2020-10-01 AND created <= 2021-04-01 AND reporter in (\"pratik.sisodia@wingify.com\")";
        //String query = "issuekey=QF-9437";
        Response jqlResponse = Jira.getJQLSearchResults(query);
        TestUtilFunctions.validateStatusCode(jqlResponse, 200);
        String totalIssues = TestUtilFunctions.getJsonValue(jqlResponse, "total").toString();
        int count = Integer.parseInt(totalIssues);
        String listOfSlaMisses ="";

        for (int i = 0; i < count; i++) {
            try {
                //get created Date for this issue
                // get comment date
                //add key to a list in case SLA not met
                String key = TestUtilFunctions.getJsonValue(jqlResponse, "issues["+i+"].key").toString();
                String issueCreatedDate = TestUtilFunctions.getJsonValue(jqlResponse, "issues["+i+"].fields.created").toString();
                Response jqlResponseComments = Jira.getCommentsForKey(key);
                String commentCreatedDate = TestUtilFunctions.getJsonValue(jqlResponseComments, "comments[0].created").toString();
                String firstCommentAuthor = TestUtilFunctions.getJsonValue(jqlResponseComments, "comments[0].author.emailAddress").toString();

                SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date issueCreatedDate1=formatter.parse(issueCreatedDate);
                Date commentCreatedDate1=formatter.parse(commentCreatedDate);

                //find hours to next working day block starts
                Calendar c = Calendar.getInstance();
                c.setTime(issueCreatedDate1);
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
                System.out.println(dayOfWeek+"======================================="+hourOfDay);
                int xHours = 0;
                if (dayOfWeek==2 || dayOfWeek==3 || dayOfWeek==4 || dayOfWeek==5){
                    if (hourOfDay >12 ){
                        xHours =24-hourOfDay+11;
                    }
                    else if (12-hourOfDay<4 ){
                        xHours = 5+11-(12-hourOfDay);
                    }
                    else
                        xHours=0;
                }
                if (dayOfWeek==6){
                    if (hourOfDay >12 ){
                        xHours =72-hourOfDay+11;
                    }
                    else if (12-hourOfDay<4 ){
                        xHours = 72+5+11-(12-hourOfDay);
                    }
                    else
                        xHours=0;
                }
                if (dayOfWeek==1){
                    xHours =24-hourOfDay+11;
                }
                if (dayOfWeek==7){
                    xHours =48-hourOfDay+11;
                }

                //find hours to next working day block ends
                long difference_In_Time
                        = commentCreatedDate1.getTime() - issueCreatedDate1.getTime();
                long difference_In_Hours
                        = ((difference_In_Time
                        / (1000 * 60 * 60))
                        % 24)-xHours;
                if (difference_In_Hours >4){
                    System.out.println(difference_In_Hours);
                    listOfSlaMisses = listOfSlaMisses+key+",";
                }
                System.out.println(i+"==============================="+listOfSlaMisses);
            } catch (NullPointerException | ParseException e) { //in case any required field comes up as blank
                System.out.println("+++++++++++++++++++++++" + TestUtilFunctions.getJsonValue(jqlResponse, "issues[" + i + "].key").toString());
                e.printStackTrace();
            }
        }
        System.out.println(listOfSlaMisses);
        //Step-3 Push message to Slack
        String messageToSlack = "Possible first response  SLA Misses: "+listOfSlaMisses +" <@U011ERWQZ7G>";
        Response slackMessage = SlackUtils.sendMessageToSlack(channelName, messageToSlack);
        TestUtilFunctions.validateStatusCode(slackMessage, 200);
    }
    @Test(groups = "g4")
    public static void createJiraIssue(){
          String username = System.getProperty("emailUsername");
          String password = System.getProperty("emailPassword");
          checkEmail.check(host,username,password);

    }
}
