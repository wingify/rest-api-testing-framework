package utils.Jira;

import basepackage.BaseApi;
import constants.BasePath;
import io.restassured.response.Response;
import java.util.HashMap;
import org.json.simple.JSONObject;

/**
 * This helper class has helper functions for pushing messages to slack
 */

public class SlackUtils {

  static final String mango = System.getenv("mango");

  /**
   * Returns API Response for message pushed to slack channel
   */
  public static Response sendMessageToSlack(
    String channelName,
    String message
  ) {
    BaseApi api = new BaseApi();
    JSONObject requestParams = new JSONObject();

    requestParams.put("to", channelName);
    requestParams.put("message", message);
    requestParams.put("mango", mango);
    api.setRequestParams(
      requestParams.toString(),
      BaseApi.MethodType.POST,
      "", //LocalConfigs.baseURI,
      BasePath.SLACK_MESSAGE
    );
    HashMap headers = new HashMap();
    headers.put(
      "Cookie",
      ""
    );
    api.setCustomHeaders(headers);
    Response response = api.execute();
    return response;
  }

  public static String getUserIDbyEmail(String email) {
    System.out.println("Users" + email);
    BaseApi baseApi = new BaseApi();
    JSONObject requestParams = new JSONObject();

    requestParams.put("email", email);
    requestParams.put("mango", mango);

    baseApi.setRequestParams(
      requestParams.toString(),
      BaseApi.MethodType.POST,
      "", //LocalConfigs.baseURI,
      BasePath.SLACK_USER
    );
    System.out.println("Response" + baseApi.execute().asString());
    return baseApi.execute().jsonPath().getString("user.id");
  }
}
