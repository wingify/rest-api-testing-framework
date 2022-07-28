package utils.GoogleSheets;

import basepackage.BaseApi;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import io.restassured.response.Response;
import utils.LocalConfigs;
import utils.TestUtilFunctions;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Properties;

public class SheetsServiceUtil {
    private static final String APPLICATION_NAME = "Google Sheets";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static String refreshToken = "";
    private static String tokenUrl = "https://oauth2.googleapis.com/";

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
         final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
         Credential credentials =GoogleAuthorizeUtil.authorize(HTTP_TRANSPORT);
         refreshToken = credentials.getRefreshToken();
         if (refreshToken ==null){
             refreshToken = LocalConfigs.getProperty("refreshToken");
         }
         else {
             LocalConfigs.setProperty("refreshToken",refreshToken);
         }
         String accessToken = getRefreshAccessToken();
         credentials.setAccessToken(accessToken);
         return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    public static String getRefreshAccessToken()  {
        String secret = LocalConfigs.jiraSecret;
        String clientId = TestUtilFunctions.decodeBase64(LocalConfigs.jiraClientId);
        BaseApi baseApi = new BaseApi();
        HashMap queryMap = new HashMap();
        queryMap.put("client_id",clientId);
        queryMap.put("client_secret",secret);
        queryMap.put("grant_type","refresh_token");
        queryMap.put("refresh_token",refreshToken);
        baseApi.addQueryParam(queryMap);
        baseApi.setRequestParams(BaseApi.MethodType.POST,tokenUrl,"token");
        Response response = baseApi.execute();
        String accessToken = (String) TestUtilFunctions.getJsonValue(response,"access_token");
        return accessToken;
    }


}