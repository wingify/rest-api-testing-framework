package basepackage;

import constants.BasePath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeSuite;
import utils.LocalConfigs;
import utils.TestUtilFunctions;

import java.util.Map;

public class BaseTest {
    public static Map cookies;
    public static int accountId;

    @BeforeSuite
    public static void login() {
        String username = System.getProperty(
                "username"
        ), password = System.getProperty("password");
        JSONObject loginJson = TestUtilFunctions.getJsonObject("login.json");
        loginJson.put("username", username);
        loginJson.put("password", password);
        BaseApi api = new BaseApi();
        api.setRequestParams(
                loginJson,
                BaseApi.MethodType.POST,
                LocalConfigs.baseURI,
                BasePath.LOGIN_PATH
        );
        Response response = api.execute();
        if (response != null) {
            cookies = response.getCookies();
            accountId = (int) TestUtilFunctions.getJsonValue(response, "accountId");
        }
    }
}
