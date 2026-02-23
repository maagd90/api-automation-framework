package com.automation.listeners;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG lifecycle listener that logs test events and enriches Allure reports.
 *
 * <p>Register this listener in {@code testng.xml} or via the {@code @Listeners} annotation:
 * <pre>{@code
 * // testng.xml
 * <listeners>
 *   <listener class-name="com.automation.listeners.TestListener"/>
 * </listeners>
 *
 * // Or per-class
 * @Listeners(TestListener.class)
 * public class MyTests { ... }
 * }</pre>
 *
 * <p>The listener produces structured log output for each test phase and updates the
 * Allure step description at test start.
 *
 * @see org.testng.ITestListener
 */
public class TestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);

    /**
     * Called when the test suite starts.
     *
     * @param context the suite execution context
     */
    @Override
    public void onStart(ITestContext context) {
        logger.info("=== Test Suite Started: {} ===", context.getName());
    }

    /**
     * Called when the test suite finishes. Logs the pass / fail / skip counts.
     *
     * @param context the suite execution context
     */
    @Override
    public void onFinish(ITestContext context) {
        logger.info("=== Test Suite Finished: {} ===", context.getName());
        logger.info("Passed: {}, Failed: {}, Skipped: {}",
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    /**
     * Called immediately before a test method is invoked. Adds the test name to the
     * Allure report description.
     *
     * @param result metadata about the test that is about to run
     */
    @Override
    public void onTestStart(ITestResult result) {
        logger.info("--- Test Started: {} ---", getTestName(result));
        Allure.description("Test: " + getTestName(result));
    }

    /**
     * Called when a test method passes. Logs the execution time.
     *
     * @param result metadata about the test that has just passed
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("✓ PASSED: {} ({}ms)", getTestName(result), result.getEndMillis() - result.getStartMillis());
    }

    /**
     * Called when a test method fails. Logs the failure message and, at DEBUG level, the
     * full stack trace.
     *
     * @param result metadata about the test that has just failed
     */
    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("✗ FAILED: {} - {}", getTestName(result),
                result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error");
        if (result.getThrowable() != null) {
            logger.debug("Stack trace:", result.getThrowable());
        }
    }

    /**
     * Called when a test method is skipped (e.g. due to a failed dependency).
     *
     * @param result metadata about the test that was skipped
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("⊘ SKIPPED: {}", getTestName(result));
    }

    /**
     * Called when a test fails but is within the configured success percentage.
     *
     * @param result metadata about the test
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("~ FAILED WITHIN SUCCESS PERCENTAGE: {}", getTestName(result));
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Returns a human-readable test name in the form {@code ClassName.methodName}.
     *
     * @param result the test result
     * @return the qualified test name
     */
    private String getTestName(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    }
}
