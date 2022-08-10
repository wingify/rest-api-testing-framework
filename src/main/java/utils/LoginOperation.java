package utils;

import basepackage.BaseApi;
import constants.BasePath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;

public class LoginOperation {

  public static Response postLogin(){
    BaseApi api = new BaseApi();
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("username", "username");
    jsonObject.put("password", "password");
    api.setRequestParams(jsonObject,
      BaseApi.MethodType.POST,
      LocalConfigs.baseURI,
      BasePath.LOGIN_PATH);
    return api.execute();
  }
}
