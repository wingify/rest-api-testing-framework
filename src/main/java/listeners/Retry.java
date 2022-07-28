package listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import utils.LocalConfigs;

public class Retry implements IRetryAnalyzer {
    public static ThreadLocal<Integer> threadCounter = new ThreadLocal<Integer>() {

        protected Integer initialValue() {
            return 0;
        }
    };

    public boolean retry(ITestResult result) {
        String retryLimit = LocalConfigs.retryCount;
        int counter = threadCounter.get();
        if (counter < Integer.valueOf(retryLimit)) {
            counter++;
            threadCounter.set(counter);
            System.out.println(true);
            return true;
        }
        threadCounter.set(0);
        return false;
    }
}
