package testclasses;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import utils.LoginOperation;
import utils.TestUtilFunctions;

public class LoginTest {

  @Test
  public void loginTest(){
    Response loginTest = LoginOperation.postLogin();
    TestUtilFunctions.validateStatusCode(loginTest, 200);
  }
}
