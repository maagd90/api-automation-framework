package com.automation.listeners;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * TestNG listener that provides enhanced Allure reporting with step tracking,
 * feature tagging, and failure attachments.
 *
 * <p>This listener integrates with the Allure reporting framework to produce
 * detailed test lifecycle information including pass/fail/skip step tracking
 * and failure attachments for easier debugging.</p>
 *
 * @author api-automation-framework
 * @version 1.0.0
 * @see org.testng.ITestListener
 */
public class AllureListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(AllureListener.class);

    /**
     * Called before any test in the suite runs.
     *
     * @param context the TestNG context for the suite that is starting
     */
    @Override
    public void onStart(ITestContext context) {
        logger.info("=== Allure Listener: Test Suite Started: {} ===", context.getName());
    }

    /**
     * Called after all tests in the suite have finished.
     *
     * @param context the TestNG context for the suite that has finished
     */
    @Override
    public void onFinish(ITestContext context) {
        logger.info("=== Allure Listener: Test Suite Finished: {} ===", context.getName());
    }

    /**
     * Called immediately before each test method executes.
     * Adds a step to the Allure report indicating the test has started.
     *
     * @param result the result object for the test that is about to start
     */
    @Override
    public void onTestStart(ITestResult result) {
        String testName = getTestName(result);
        logger.info("Allure Listener: Test Started: {}", testName);
        Allure.getLifecycle().startStep(
                UUID.randomUUID().toString(),
                new StepResult().setName("Test started: " + testName).setStatus(Status.PASSED)
        );
        Allure.getLifecycle().stopStep();
    }

    /**
     * Called when a test method completes successfully.
     * Adds a PASSED step to the Allure report.
     *
     * @param result the result object for the test that passed
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = getTestName(result);
        logger.info("Allure Listener: ✓ PASSED: {}", testName);
        Allure.getLifecycle().startStep(
                UUID.randomUUID().toString(),
                new StepResult().setName("PASSED: " + testName).setStatus(Status.PASSED)
        );
        Allure.getLifecycle().stopStep();
    }

    /**
     * Called when a test method fails.
     * Adds a FAILED step and attaches the failure details to the Allure report.
     *
     * @param result the result object for the test that failed
     */
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = getTestName(result);
        logger.error("Allure Listener: ✗ FAILED: {}", testName);
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            String failureDetails = throwable.getMessage() != null ? throwable.getMessage() : "Unknown error";
            Allure.addAttachment("Failure Details", "text/plain",
                    new ByteArrayInputStream(failureDetails.getBytes(StandardCharsets.UTF_8)),
                    ".txt");
        }
        Allure.getLifecycle().startStep(
                UUID.randomUUID().toString(),
                new StepResult().setName("FAILED: " + testName).setStatus(Status.FAILED)
        );
        Allure.getLifecycle().stopStep();
    }

    /**
     * Called when a test method is skipped.
     * Adds a SKIPPED step to the Allure report.
     *
     * @param result the result object for the test that was skipped
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = getTestName(result);
        logger.warn("Allure Listener: ⊘ SKIPPED: {}", testName);
        Allure.getLifecycle().startStep(
                UUID.randomUUID().toString(),
                new StepResult().setName("SKIPPED: " + testName).setStatus(Status.SKIPPED)
        );
        Allure.getLifecycle().stopStep();
    }

    /**
     * Called when a test method fails but remains within the configured
     * success-percentage threshold.
     *
     * @param result the result object for the test
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("Allure Listener: ~ FAILED WITHIN SUCCESS PERCENTAGE: {}", getTestName(result));
    }

    /**
     * Builds a human-readable test identifier in the form {@code "ClassName.methodName"}.
     *
     * @param result the TestNG result object from which the name is derived
     * @return a non-null string of the form {@code "ClassName.methodName"}
     */
    private String getTestName(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    }
}
