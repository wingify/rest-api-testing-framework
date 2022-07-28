package reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

public class ExtentReport {
    private static final String fileSeperator = System.getProperty(
            "file.separator"
    );
    private static final String reportFileName = "API-Test-Report" + ".html";
    private static final String reportFilepath =
            System.getProperty("user.dir") + fileSeperator;
    private static final String reportFileLocation =
            reportFilepath + fileSeperator + reportFileName;
    private static ExtentReports reports = null;

    public static ExtentReports CreateInstance() {
        String fileName = getReportPath(reportFilepath);
        ExtentHtmlReporter reporter = new ExtentHtmlReporter(fileName);
        ThreadLocal<ExtentReports> extentReportsThreadLocal = new ThreadLocal<ExtentReports>() {

            protected ExtentReports initialValue() {
                ExtentReports reports = new ExtentReports();
                return reports;
            }
        };
        reporter.config().setTheme(Theme.DARK);
        reporter.config().setReportName("API Automation");
        ExtentReports reports = extentReportsThreadLocal.get();
        reports.attachReporter(reporter);

        return reports;
    }

    public static ExtentReports getInstance() {
        if (reports == null) {
            reports = CreateInstance();
        }
        return reports;
    }

    private static String getReportPath(String path) {
        File testDirectory = new File(path);
        if (!testDirectory.exists()) {
            if (testDirectory.mkdir()) {
                System.out.println("Directory: " + path + " is created!");
            } else {
                System.out.println("Failed to create directory: " + path);
            }
        } else {
            System.out.println("Directory already exists: " + path);
        }
        return reportFileLocation;
    }

    public static void info(String s) {
        ExtentTestManager.getTest().info(s);
    }

    public static void warning(String s) {
        ExtentTestManager.getTest().warning(s);
    }

    public static void error(String s) {
        ExtentTestManager.getTest().error(s);
    }
}
