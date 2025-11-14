package tests;

import base.TestBase;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;
import utils.TestUtils;

@Epic("Login Functionality")
@Feature("User Authentication")
public class LoginTest extends TestBase {
    private LoginPage loginPage;

    @BeforeMethod(description = "Test Setup: Initialize test and handle welcome screen")
    @Story("Test Setup")
    public void setupTest() {
        try {
            TestUtils.log("Starting test setup");
            
            
            loginPage = new LoginPage();
            loginPage.skipWelcomeScreen();
            
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                TestUtils.log("Thread was interrupted during sleep", "WARN");
            }
            
            // Take a screenshot after setup
            TestUtils.takeScreenshot(driver, "After_Setup");
            
        } catch (Exception e) {
            TestUtils.log("Error during test setup: " + e.getMessage(), "ERROR");
            throw new RuntimeException("Test setup failed", e);
        }
    }

    @Test(priority = 1, description = "Verify successful login with valid credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test Description: Verify user can login with valid credentials")
    @Story("Valid Login")
    public void testSuccessfulLogin() {
        try {
            // Get test data from configuration
            String email = config.getValidEmail();
            String password = config.getValidPassword();
            
            // Log test start with environment info
            TestUtils.log("Starting successful login test");
            TestUtils.log(String.format("Environment: %s", config.getEnvironment()));
            TestUtils.log(String.format("Using email: %s", email));
            
            // Perform login
            loginPage.login(email, password);
            
            // Take screenshot if configured
            if (config.isScreenshotOnPass()) {
                TestUtils.takeScreenshot(driver, "After_Login_Success");
            }
            
            TestUtils.log("Successfully logged in with valid credentials", "PASS");
            
        } catch (Exception e) {
            // Take screenshot on failure if configured
            if (config.isScreenshotOnFail()) {
                TestUtils.takeScreenshot(driver, "Login_Test_Failed");
            }
            TestUtils.log("Login test failed: " + e.getMessage(), "ERROR");
            throw e;
        }
    }

}
