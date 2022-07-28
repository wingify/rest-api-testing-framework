package reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import java.util.HashMap;
import java.util.Map;

public class ExtentTestManager {
    static Map<Integer, ExtentTest> extentTestMap = new HashMap<Integer, ExtentTest>();
    static ExtentReports extent = ExtentReport.getInstance();

    public static synchronized ExtentTest getTest() {
        return extentTestMap.get(
                (int) Thread.currentThread().getId()
        );
    }

    public static synchronized void endTest() {
        extent.flush();
    }

    public static ExtentTest startTest(final String testName) {
        final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<ExtentTest>() {

            protected ExtentTest initialValue() {
                ExtentTest extentTest = extent.createTest(testName);
                return extentTest;
            }
        };
        ExtentTest test = testThreadLocal.get();
        extentTestMap.put((int) Thread.currentThread().getId(), test);
        return test;
    }
}
