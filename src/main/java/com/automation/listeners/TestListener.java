package com.automation.listeners;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener that integrates structured logging and Allure reporting into the
 * test lifecycle.
 *
 * <p>This listener is registered on test classes via the TestNG
 * {@code @Listeners(TestListener.class)} annotation. It hooks into the following
 * lifecycle events:</p>
 * <ul>
 *   <li>{@link #onStart(ITestContext)} – logs when a test suite begins.</li>
 *   <li>{@link #onFinish(ITestContext)} – logs a pass/fail/skip summary when a suite ends.</li>
 *   <li>{@link #onTestStart(ITestResult)} – logs the start of each individual test and
 *       attaches a description to the Allure report.</li>
 *   <li>{@link #onTestSuccess(ITestResult)} – logs a ✓ PASSED line with elapsed time.</li>
 *   <li>{@link #onTestFailure(ITestResult)} – logs a ✗ FAILED line with the error message
 *       and (at DEBUG level) the full stack trace.</li>
 *   <li>{@link #onTestSkipped(ITestResult)} – logs a ⊘ SKIPPED line.</li>
 *   <li>{@link #onTestFailedButWithinSuccessPercentage(ITestResult)} – logs a warning for
 *       tests that failed but remained within an acceptable success percentage.</li>
 * </ul>
 *
 * <p>Usage example (on a test class):</p>
 * <pre>{@code
 * @Listeners(TestListener.class)
 * public class PostTests {
 *     // ...
 * }
 * }</pre>
 *
 * @author api-automation-framework
 * @version 1.0.0
 * @see org.testng.ITestListener
 */
public class TestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);

    /**
     * Called before any test in the suite runs.
     *
     * <p>Logs the suite name at INFO level.</p>
     *
     * @param context the TestNG context for the suite that is starting (never {@code null})
     */
    @Override
    public void onStart(ITestContext context) {
        logger.info("=== Test Suite Started: {} ===", context.getName());
    }

    /**
     * Called after all tests in the suite have finished.
     *
     * <p>Logs a summary line containing the counts of passed, failed, and skipped tests.</p>
     *
     * @param context the TestNG context for the suite that has finished (never {@code null})
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
     * Called immediately before each test method executes.
     *
     * <p>Logs the test name at INFO level and attaches the test name as an Allure
     * description for richer report output.</p>
     *
     * @param result the result object for the test that is about to start (never {@code null})
     */
    @Override
    public void onTestStart(ITestResult result) {
        logger.info("--- Test Started: {} ---", getTestName(result));
        Allure.description("Test: " + getTestName(result));
    }

    /**
     * Called when a test method completes successfully.
     *
     * <p>Logs a ✓ PASSED line including the elapsed time in milliseconds.</p>
     *
     * @param result the result object for the test that passed (never {@code null})
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("✓ PASSED: {} ({}ms)", getTestName(result), result.getEndMillis() - result.getStartMillis());
    }

    /**
     * Called when a test method fails.
     *
     * <p>Logs a ✗ FAILED line with the exception message at ERROR level.
     * The full stack trace is logged at DEBUG level if a throwable is attached.</p>
     *
     * @param result the result object for the test that failed (never {@code null})
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
     * <p>Logs a ⊘ SKIPPED line at WARN level.</p>
     *
     * @param result the result object for the test that was skipped (never {@code null})
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("⊘ SKIPPED: {}", getTestName(result));
    }

    /**
     * Called when a test method fails but the failure is within the configured
     * success-percentage threshold ({@code @Test(successPercentage = ...)}).
     *
     * <p>Logs a warning line at WARN level.</p>
     *
     * @param result the result object for the test (never {@code null})
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("~ FAILED WITHIN SUCCESS PERCENTAGE: {}", getTestName(result));
    }

    /**
     * Builds a human-readable test identifier in the form
     * {@code "ClassName.methodName"}.
     *
     * @param result the TestNG result object from which the name is derived
     * @return a non-null string of the form {@code "fully.qualified.ClassName.methodName"}
     */
    private String getTestName(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    }
}
