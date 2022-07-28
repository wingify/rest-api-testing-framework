package listeners;

import com.aventstack.extentreports.Status;
import org.testng.IRetryAnalyzer;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;
import reports.ExtentTestManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class DefaultListeners extends ListenerDecorator {

    public void onStart(ITestContext context) {
        System.out.println("*** Test Suite " + context.getName() + " started ***");
    }

    public void onFinish(ITestContext context) {
        System.out.println(("*** Test Suite " + context.getName() + " ending ***"));
        ExtentTestManager.endTest();
    }

    public void onTestStart(ITestResult result) {
        System.out.println(
                "Running test method " + result.getMethod().getMethodName() + ".."
        );
        ExtentTestManager.startTest(result.getMethod().getMethodName());
    }

    public void onTestSuccess(ITestResult result) {
        System.out.println(
                "*** Executed " + result.getMethod().getMethodName() + "Test Successful"
        );
        ExtentTestManager.getTest().log(Status.PASS, "Test passed");
        Retry.threadCounter.set(0);
    }

    public void onTestFailure(ITestResult result) {
        System.out.println(
                "*** Test execution " + result.getMethod().getMethodName() + " failed..."
        );
        ExtentTestManager
                .getTest()
                .log(Status.FAIL, result.getThrowable().toString());
    }

    public void onTestSkipped(ITestResult result) {
        System.out.println(
                "*** Test " + result.getMethod().getMethodName() + " skipped..."
        );
        ExtentTestManager.getTest().log(Status.SKIP, "Test Skipped");
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println(
                "*** Test failed but within percentage % " +
                        result.getMethod().getMethodName()
        );
    }

    public void transform(
            ITestAnnotation annotation,
            Class testClass,
            Constructor testConstructor,
            Method testMethod
    ) {
        IRetryAnalyzer retryAnalyzer = null;
        if (retryAnalyzer == null) {
            annotation.setRetryAnalyzer(Retry.class);
        }
    }
}
