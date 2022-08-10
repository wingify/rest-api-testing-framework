package utils;

import basepackage.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import org.assertj.core.api.Assertions;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * Class contains All Utility functions required in the testcases
 */

public class TestUtilFunctions extends BaseTest {

    /**
     * Validate Json Schema in reponse with schema file passed
     *
     * @param response Response Object
     * @param schema   Json Schema Name
     */
    public static void validateJsonSchema(Response response, String schema) {
        response
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("testschema/" + schema));
    }

    /**
     * Validate API Response code
     *
     * @param response Response Object
     * @param status   response status code
     */

    public static void validateStatusCode(Response response, int status) {
        int code = response.getStatusCode();
        Assertions
                .assertThat(code)
                .isEqualTo(status)
                .withFailMessage(response.getStatusLine());
    }

    /**
     * Returns json value for provided json path
     *
     * @param response Response Object
     * @param path     JsonPath
     */
    public static Object getJsonValue(Response response, String path) {
        return response.jsonPath().get(path);
    }

    /**
     * Returns JsonObject for filename passed
     *
     * @param filename
     */

    public static JSONObject getJsonObject(String filename) {
        JSONObject jsonObject = null;
        try {
            String basePath =
                    System.getProperty("user.dir") + "/src/test/resources/testdata/";
            JSONParser parser = new JSONParser();
            Reader reader = new FileReader(basePath + filename);
            jsonObject = (org.json.simple.JSONObject) parser.parse(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static String getTimeStamp() {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        return ts.toString();
    }

    public static String getRandomNumber() {
        Random random = new Random();
        int num = random.nextInt(999999);
        return String.format("%06d", num);
    }

    /**
     * Returns List by parsing csv file passed
     *
     * @param name Filename
     */
    public static List<String[]> readCsv(String name) {
        Reader reader = null;
        List<String[]> list = null;
        try {
            reader =
                    new FileReader(
                            new File(
                                    System.getProperty("user.dir") +
                                            "/src/test/resources/testdata/" +
                                            name
                            )
                    );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        CSVReader csvReader = new CSVReader(reader);
        try {
            list = csvReader.readAll();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Map Json Passed with the class object and returns filed object
     *
     * @param filename
     * @param obj      Object
     */
    public static Object mapJson(String filename, Object obj) {
        Object requestPOJO = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String filePath =
                    System.getProperty("user.dir") +
                            "/src/test/resources/testdata/" +
                            filename;
            File requestJSONFile = new File(filePath);
            requestPOJO = mapper.readValue(requestJSONFile, obj.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestPOJO;
    }

    public static Object[] addElement(Object[] a, Object e) {
        a = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    public static String getKey() {
        String key = "_" + getRandomNumber();
        return key;
    }

    public static String decodeBase64(String clientId) {
        String encodedString = clientId;
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String decodedString = new String(decodedBytes);
        return decodedString;
    }

    public static void removeHeader(
            String header,
            FilterableRequestSpecification filterableRequestSpecification
    ) {
        filterableRequestSpecification.removeHeader(header);
    }

    public static void removeCookies(
            FilterableRequestSpecification filterableRequestSpecification
    ) {
        filterableRequestSpecification.removeCookies();
    }

    /**
     * get v2 request paths for a specified path
     *
     * @param pathName the path requested
     * @return {@link String} apiv2 pathname
     */
    public static String getV2Paths(String pathName) {
        return "api/v2/" + pathName;
    }
}
