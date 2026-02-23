package com.automation.listeners;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        logger.info("=== Test Suite Started: {} ===", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("=== Test Suite Finished: {} ===", context.getName());
        logger.info("Passed: {}, Failed: {}, Skipped: {}",
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("--- Test Started: {} ---", getTestName(result));
        Allure.description("Test: " + getTestName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("✓ PASSED: {} ({}ms)", getTestName(result), result.getEndMillis() - result.getStartMillis());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("✗ FAILED: {} - {}", getTestName(result),
                result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error");
        if (result.getThrowable() != null) {
            logger.debug("Stack trace:", result.getThrowable());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("⊘ SKIPPED: {}", getTestName(result));
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("~ FAILED WITHIN SUCCESS PERCENTAGE: {}", getTestName(result));
    }

    private String getTestName(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    }
}
