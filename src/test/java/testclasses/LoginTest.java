package testclasses;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import utils.LoginOperation;
import utils.TestUtilFunctions;

public class LoginTest {

  @Test
  public void loginTest(){
    Response loginResponse = LoginOperation.login();
    TestUtilFunctions.validateStatusCode(loginResponse, 200);
  }
}
