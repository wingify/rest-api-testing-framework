package basepackage;

import com.github.dzieciou.testing.curl.CurlLoggingRestAssuredConfigFactory;
import constants.BasePath;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import listeners.TestNgListeners;
import utils.BuildIAPRequest;
import utils.LocalConfigs;
import utils.TestUtilFunctions;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains wrapper functions for RestAssured.
 */
public class BaseApi {

    private final Map cookies = BaseTest.cookies;
    private final RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
    private HashMap headerMap;
    private MethodType method;

    public BaseApi() {
        setHeaders(ContentType.JSON);
    }

    public MethodType getMethod() {
        return this.method;
    }

    public void setMethod(BaseApi.MethodType method) {
        this.method = method;
    }

    public RequestSpecBuilder getRequestSpecBuilder() {
        return this.requestSpecBuilder;
    }

    /**
     * Executes the api request created using reqspecbuilder
     */
    public Response execute() {
        RestAssuredConfig config=null;
        RequestSpecification requestSpecification = this.requestSpecBuilder.build();

        RestAssured.defaultParser = Parser.JSON;
        if (System.getProperty("isCurlLoggingEnabled").equals("false")){
             config = new RestAssuredConfig();
        }
        else {
             config = CurlLoggingRestAssuredConfigFactory.createConfig();
        }

        Response response;
        switch (this.method) {
            case GET:
                response =
                        RestAssured
                                .given()
                                .config(config)
                                .spec(requestSpecification)
                                .when()
                                .get();
                break;
            case POST:
                response =
                        RestAssured
                                .given()
                                .config(config)
                                .spec(requestSpecification)
                                .when()
                                .post();
                break;
            case PUT:
                response =
                        RestAssured
                                .given()
                                .config(config)
                                .spec(requestSpecification)
                                .when()
                                .put();
                break;
            case DELETE:
                response =
                        RestAssured
                                .given()
                                .config(config)
                                .spec(requestSpecification)
                                .when()
                                .delete();
                break;
            case PATCH:
                response =
                        RestAssured
                                .given()
                                .config(config)
                                .spec(requestSpecification)
                                .when()
                                .patch();
                break;
            default:
                throw new RuntimeException("API method not specified");
        }
        return response;
    }

    /**
     * set Default Headers for an API Request
     */

    public void setHeaders(ContentType content) {
        try {
            headerMap = new HashMap();
            if (cookies != null) {
                getRequestSpecBuilder().addCookies(cookies);
            }
            getRequestSpecBuilder().addHeaders(headerMap);
            getRequestSpecBuilder().setContentType(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCustomHeaders(HashMap map) {
        headerMap.putAll(map);
        getRequestSpecBuilder().addHeaders(headerMap);
    }

    /**
     * Sets the Request Params
     *
     * @param body     RequestBody
     * @param method   RequestMethod
     * @param Uri      BaseURI
     * @param basePath BasePath of API
     */
    public void setRequestParams(
            Object body,
            MethodType method,
            String Uri,
            String basePath
    ) {
        getRequestSpecBuilder().setBody(body);
        setMethod(method);
        getRequestSpecBuilder().setBasePath(basePath);
        getRequestSpecBuilder().setBaseUri(Uri);
    }

    /**
     * Sets the Request Params
     *
     * @param method   RequestMethod
     * @param Uri      BaseURI
     * @param basePath BasePath of API
     */
    public void setRequestParams(MethodType method, String Uri, String basePath) {
        setMethod(method);
        getRequestSpecBuilder().setBasePath(basePath);
        getRequestSpecBuilder().setBaseUri(Uri);
    }
    /**
     * Sets the Request Params
     *
     * @param queryMap hashmap containing query params
     */
    public void addQueryParam(HashMap queryMap) {
        getRequestSpecBuilder().addQueryParams(queryMap);
    }

    public enum MethodType {
        POST,
        GET,
        PUT,
        DELETE,
        PATCH;

        MethodType() {
        }
    }
}
