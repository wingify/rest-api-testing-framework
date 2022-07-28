package listeners;

import org.testng.*;

public interface TestNgListeners
        extends
        ITestListener,
        ISuiteListener,
        IInvokedMethodListener,
        IRetryAnalyzer,
        IAnnotationTransformer,
        IConfigurationListener,
        IExecutionListener {
}
