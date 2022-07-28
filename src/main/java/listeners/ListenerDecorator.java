package listeners;

import org.testng.IInvokedMethod;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public abstract class ListenerDecorator implements TestNgListeners {

    public void transform(
            ITestAnnotation annotation,
            Class testClass,
            Constructor testConstructor,
            Method testMethod
    ) {
    }

    public void onConfigurationSuccess(ITestResult itr) {
    }

    public void onConfigurationFailure(ITestResult itr) {
    }

    public void onConfigurationSkip(ITestResult itr) {
    }

    public void onExecutionStart() {
    }

    public void onExecutionFinish() {
    }

    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
    }

    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
    }

    public boolean retry(ITestResult result) {
        return false;
    }

    public void onStart(ISuite suite) {
    }

    public void onFinish(ISuite suite) {
    }

    public void onTestStart(ITestResult result) {
    }

    public void onTestSuccess(ITestResult result) {
    }

    public void onTestFailure(ITestResult result) {
    }

    public void onTestSkipped(ITestResult result) {
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    public void onStart(ITestContext context) {
    }

    public void onFinish(ITestContext context) {
    }
}
