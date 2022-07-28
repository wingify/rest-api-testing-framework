package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Class sets config property variables and loads them in memory on initialisation.
 */

public class LocalConfigs {
    public static String retryCount;
    public static String baseURI;
    public static int variationCount;
    public static String clientId;
    static Properties properties;
    public static String jiraClientId;
    public static String jiraSecret;
    public static String refreshToken;


    static {
        baseURI = System.getProperty("appUrl");
        retryCount = getProperty("retryCount");
        variationCount = (Integer.valueOf(getProperty("variationCount")));
        clientId = getProperty("clientId");
        jiraClientId=getProperty("jiraClientId");
        jiraSecret=getProperty("jiraSecret");
        refreshToken=getProperty("refreshToken");

        if (baseURI==null){
            baseURI="";

        }
    }

    private static void loadProperty() throws IOException {
        if (properties == null) {
            properties = new Properties();
            properties.load(
                    new FileInputStream(
                            System.getProperty("user.dir") +
                                    "/src/main/resources/configs/config.properties"
                    )
            );
        }
    }

    public static String getProperty(String property) {
        try {
            loadProperty();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return properties.getProperty(property);
    }
    public static void setProperty(String property,String value) {
        try {
            loadProperty();
            properties.setProperty(property,value);
            FileWriter writer = new FileWriter(new File(
                    System.getProperty("user.dir") +
                            "/src/main/resources/configs/config.properties"));
            properties.store(writer,"Property Added");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
